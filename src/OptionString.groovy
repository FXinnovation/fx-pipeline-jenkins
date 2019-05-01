class OptionString implements Serializable {
  private String value = ''
  private String delimiter = ' '
  private groovy.lang.Script context

  OptionString(groovy.lang.Script context) {
    this.context = context
  }

  void add(String optionName, Object optionValue = '', Class<?> expectedOptionType = CharSequence) {
    if (!(expectedOptionType.isInstance(optionValue))) {
      this.context.error("Cannot add option “${optionName}” to the option string. It is expected to be “${expectedOptionType}”, given: “${optionValue.getClass()}”.")
    }

    if (optionValue instanceof AbstractCollection) {
      for(singleOptionValue in optionValue) {
        this.doAddOption(optionName, singleOptionValue)
      }
    } else {
      this.doAddOption(optionName, optionValue)
    }
  }

  void setDelimiter(String delimiter) {
    this.delimiter = delimiter
  }

  String getDelimiter() {
    return this.delimiter
  }

  String toString() {
    return this.value
  }

  private void doAddOption(String optionName, Object optionValue) {
    if (!(optionValue instanceof Serializable)) {
      this.context.error("Cannot add option “${optionName}” to the option string. The given value is does not implement Serializable, thus it cannot be converted to a string. (given: “${optionValue.getClass()}”).")
    }

    this.value += optionName.toString()

    if (optionValue) {
      this.value += this.delimiter.toString() + optionValue.toString()
    }

    this.value += ' '
  }
}
