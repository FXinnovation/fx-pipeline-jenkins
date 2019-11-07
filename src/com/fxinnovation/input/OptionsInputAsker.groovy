package com.fxinnovation.input

import com.fxinnovation.data.UserInput
import org.jenkinsci.plugins.structs.describable.UninstantiatedDescribable
import groovy.lang.Script

class OptionsInputAsker {
  final static OPTION_NONE = 0
  final static OPTION_REMOVE_DEFAULT = 1
  final static OPTION_CONVERT_EOL_TO_LIST = 2
  final static OPTION_REMOVE_DEFAULT_AND_CONVERT_EOL_TO_LIST = 3

  private Script context
  private String inputMessage = 'Please enter the following values'
  private String inputOKButtonCaption = 'Select'
  private String inputId = 'OK'

  OptionsInputAsker(Script context) {
    this.context = context
  }

  /**
   * Asks an input from the final user with X defined parameters (userInputs) in it.
   * @param userInputs
   * @param option Whether or not to: trim the result of the default values, convert values with EOL to list, etc.
   * @return Map
   */
  Map askInputs(Collection<UserInput> userInputs, Integer option = 0) {
    return this.populateInputs(
      this.context.input(
        id: this.getInputId(),
        message: this.getInputMessage(),
        ok: this.getInputOKButtonCaption(),
        parameters: userInputs.inject([]) { buffer, userInput ->
          buffer << this.transformUserInputToInputParameter(userInput)
          buffer
        }
      ),
      userInputs,
      option
    )
  }

  /**
   * Returns a populated Map with the input results, according to option chosen by user.<br>
   * Note: inputResults is an argument and the returned value.<br>
   * It means this method belongs to the “input” object given by this.context; but this.context.input is a NullObject and therefore cannot be inherited or decorated
   * @param inputResults
   * @param option
   * @return
   */
  private Map populateInputs(HashMap inputResults, Collection<UserInput> userInputs, Integer option) {
    if (0 == option) {
      return inputResults
    }

    def mutableInputResults = inputResults.clone()

    for (inputResult in inputResults) {
      def inputResultName = inputResult.key
      def inputResultValue = inputResult.value

      for (userInput in userInputs) {
        if (
          this.shouldRemoveDefault(option) &&
          userInput.getName() == inputResultName.toString() &&
          userInput.getDefaultValue().toString() == inputResultValue.toString()
        ) {
          mutableInputResults.remove(inputResultName)
        }
      }
    }

    mutableInputResults.each { inputResultName, inputResultValue ->
      if (
        this.shouldConvertEOLToList(option) &&
        inputResultValue instanceof String &&
        '' != inputResultValue
      ) {
        mutableInputResults[inputResultName] = inputResultValue.tokenize("\n")
      }
    }

    return mutableInputResults
  }

  private Boolean shouldRemoveDefault(Integer option) {
    return option & this.OPTION_REMOVE_DEFAULT
  }

  private Boolean shouldConvertEOLToList(Integer option) {
    return option & this.OPTION_CONVERT_EOL_TO_LIST
  }

  private UninstantiatedDescribable transformUserInputToInputParameter(UserInput userInput) {
    switch (userInput.getType()) {
      case CharSequence:
        return this.context.string(
          name: userInput.getName(),
          defaultValue: userInput.getDefaultValue(),
          description: userInput.getDescription(),
        )
        break
      case Boolean:
        return this.context.booleanParam(
          name: userInput.getName(),
          defaultValue: userInput.getDefaultValue(),
          description: userInput.getDescription(),
        )
        break
      case Integer:
        return this.context.string(
          name: userInput.getName(),
          defaultValue: userInput.getDefaultValue(),
          description: userInput.getDescription(),
          trim: true,
        )
        break
      case Enum:
        return this.context.choice(
          name: userInput.getName(),
          choices: userInput.getDefaultValue(),
          description: userInput.getDescription(),
        )
      case Collection:
        return this.context.text(
          name: userInput.getName(),
          choices: userInput.getDefaultValue(),
          description: userInput.getDescription(),
        )
        break
      default:
        this.context.error("Cannot find the correct “input” type for “${optionName}” with type “${optionType}”. This maybe a mistake or that the input type is not implemented yet.")
    }
  }

  // Getters/Setters

  String getInputMessage() {
    return inputMessage
  }

  OptionsInputAsker setInputMessage(String inputMessage) {
    this.inputMessage = inputMessage
    return this
  }

  String getInputOKButtonCaption() {
    return inputOKButtonCaption
  }

  OptionsInputAsker setInputOKButtonCaption(String inputOKButtonCaption) {
    this.inputOKButtonCaption = inputOKButtonCaption
    return this
  }

  String getInputId() {
    return inputId
  }

  OptionsInputAsker setInputId(String inputId) {
    this.inputId = inputId
    return this
  }
}
