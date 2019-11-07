package com.fxinnovation.factory

import com.fxinnovation.data.OptionString

class OptionStringFactory {
  private groovy.lang.Script context

  private OptionString optionString

  OptionStringFactory(groovy.lang.Script context) {
    this.context = context
  }

  void addOption(String optionName, Object optionValue = '', Class<?> expectedOptionType = CharSequence) {
    if (!(expectedOptionType.isInstance(optionValue))) {
      this.context.error("Cannot add option “${optionName}” to the option string. It is expected to be “${expectedOptionType}”, given: “${optionValue.getClass()}”.")
    }

    this.context.println(optionValue.getClass())
    if (optionValue instanceof AbstractCollection) {
      for(singleOptionValue in optionValue) {
        this.checkOption(optionName, singleOptionValue)
        this.optionString.addOption(optionName, optionValue)
      }

      return
    }

    this.checkOption(optionName, optionValue)
    this.optionString.addOption(optionName, optionValue)
  }

  OptionString createOptionString(CharSequence delimiter) {
    this.optionString = new OptionString()
    this.optionString.setDelimiter(delimiter)

    return this.optionString
  }

  OptionString getOptionString() {
    return optionString
  }

  private void checkOption(String optionName, Object optionValue) {
    if (!(optionValue instanceof Serializable)) {
      this.context.error("Cannot add option “${optionName}” to the option string. The given value is does not implement Serializable, thus it cannot be converted to a string. (given: “${optionValue.getClass()}”).")
    }
  }
}
