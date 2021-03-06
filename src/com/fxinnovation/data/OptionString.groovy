package com.fxinnovation.data

class OptionString implements Serializable {
  private String value = ''
  private String delimiter = ' '

  String getValue() {
    return value
  }

  void setValue(String value) {
    this.value = value
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

  void addOption(CharSequence optionName, Object optionValue) {
    this.value += optionName.toString()

    if (optionValue) {
      this.value += this.getDelimiter() + optionValue.toString()
    }

    this.value += ' '
  }
}
