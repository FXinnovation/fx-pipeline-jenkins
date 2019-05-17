package com.fxinnovation.utils

import com.fxinnovation.data

class OptionsInputAsker {
  private groovy.lang.Script context
  private String inputMessage = 'Please enter the following values'
  private String inputOKButtonCaption = 'Select'
  private String inputId = 'OK'

  OptionsInputAsker(groovy.lang.Script context) {
    this.context = context
  }

  /**
   * Asks an input from the final user with X defined parameters (userInputs) in it.
   * @param userInputs
   * @return
   */
  Map askInputs(Map<UserInput> userInputs) {
    return this.context.input(
      id: this.getInputId(),
      message: this.getInputMessage(),
      ok: this.getInputOKButtonCaption(),
      parameters: [
        userInputs.inject([]) { result, userInput
          return result += this.transformUserInputToInputParameter(userInput)
        }
      ]
    )
  }

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

  private UninstantiatedDescribable transformUserInputToInputParameter(UserInput userInput) {
    switch (userInput.getType()) {
      case CharSequence:
          return text(
            name: userInput.getName(),
            defaultValue: userInput.getDefaultValue(),
            description: userInput.getDescription(),
          )
        break
      case Boolean:
        return booleanParam(
          name: userInput.getName(),
          defaultValue: userInput.getDefaultValue(),
          description: userInput.getDescription(),
        )
        break
      case Integer:
        return text(
          name: userInput.getName(),
          defaultValue: userInput.getDefaultValue(),
          description: userInput.getDescription(),
        )
        break
      case Collection:
        return choice(
          name: userInput.getName(),
          choices: userInput.getDefaultValue(),
          description: userInput.getDescription(),
        )
        break
      default:
        this.context.error("Cannot find the correct “input” type for “${optionName}” with type “${optionType}”. This maybe a mistake or that the input type is not implemented yet.")
    }
  }
}
