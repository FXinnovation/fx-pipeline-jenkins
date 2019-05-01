class OptionString implements Serializable {
  private String content
  private String delimiter = ' '

  void add(String optionName, Object optionValue = '', Class<?> expectedOptionType = CharSequence) {
    if (!(expectedOptionType.isInstance(option))) {
      error("Cannot add option “${optionName}” to the option string. It is expected to be “${expectedOptionType}”, given: “${optionValue.getClass()}”.")
    }

    if (optionValue instanceof Iterable) {
      for (i = 0; i < optionValue.size(); i++){
        this.updateContent(optionName, optionValue)
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

  private void updateContent(String optionName, String optionValue) {
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
