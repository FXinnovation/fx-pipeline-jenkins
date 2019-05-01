class OptionString implements Serializable {
  private String content
  private String delimiter = ' '

  OptionString() {
  }

  void add(String optionName, Object optionValue = '', Class<?> expectedOptionType = CharSequence) {
    if (!(expectedOptionType.isInstance(optionValue))) {
      error("Cannot add option “${optionName}” to the option string. It is expected to be “${expectedOptionType}”, given: “${optionValue.getClass()}”.")
    }

    if (optionValue instanceof AbstractCollection) {
      for (singleOptionValue in optionValue){
        this.updateContent(optionName, singleOptionValue)
      }
    }

    this.updateContent(optionName, optionValue)
  }

  void setDelimiter(String delimiter) {
    this.delimiter = delimiter
  }

  String getDelimiter() {
    return this.delimiter
  }

  String toString() {
    return content
  }

  private void updateContent(String optionName, Object optionValue) {
    if (!(optionValue instanceof Serializable)) {
      error("Cannot add option “${optionName}” to the option string. The given value is does not implement Serializable, thus it cannot be converted to a string. (given: “${optionValue.getClass()}”).")
    }

    this.content += optionName

    if ('' != optionValue) {
      this.content += this.delimiter + optionValue
    }

    this.content += ' '
  }
}
