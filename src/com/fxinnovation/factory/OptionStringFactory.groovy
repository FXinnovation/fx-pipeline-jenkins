package com.fxinnovation.factory

import com.fxinnovation.data.OptionString

class OptionStringFactory {
  private groovy.lang.Script context

  private OptionString optionString

  OptionStringFactory(groovy.lang.Script context) {
    this.context = context
  }

  String toString() {
    return this.optionString.toString()
  }

  void addOption(String optionName, Object optionValue = '', Class<?> expectedOptionType = CharSequence) {
    this.checkOptionString()
    this.checkOptionValueType(optionValue, expectedOptionType)

    if (optionValue instanceof java.util.AbstractCollection) {
      for(singleOptionValue in optionValue) {
        this.checkOption(optionName, singleOptionValue)
        this.optionString.addOption(optionName, singleOptionValue)
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

  private void checkOptionString() {
    if (null == this.optionString) {
      this.context.error("Cannot call method on object ${this.getClass()} since “createOptionString” was not called. Please call “createOptionString” before manipulating this object.")
    }
  }

  private void checkOptionValueType(Object optionValue, Class<?> expectedOptionType) {
    if (!(expectedOptionType.isInstance(optionValue))) {
      this.context.error("Cannot add option “${optionName}” to the option string. It is expected to be “${expectedOptionType}”, given: “${optionValue.getClass()}”.")
    }
  }
}
