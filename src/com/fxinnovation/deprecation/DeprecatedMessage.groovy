package com.fxinnovation.deprecation

import groovy.lang.Script

class DeprecatedMessage {
  private Script context

  DeprecatedMessage(Script context) {
    this.context = context
  }

  public void displayWarningDeprecatedFunction(String oldFunctionName, String newFunctionName, String deprecationDate) {
    def deprecation = this.createDateFromParsedString(deprecationDate)
    this.context.ansiColor('xterm') {
      this.context.println(
        "\u001b[35m /!\\ DEPRECATION WARNING /!\\\n" + 
        "“\033[0;4m\033[0;1m\u001b[35m${oldFunctionName}\u001b[0m\u001b[35m” is now deprecated and will be remove on \033[0;4m\033[0;1m\u001b[35m" + this.formatToEnglishDate(deprecation) + "\u001b[0m\u001b[35m, replaced by “\033[0;4m\033[0;1m\u001b[35m${newFunctionName}\u001b[0m\u001b[35m”" +
        "\u001B[0m" 
      )
    }
  }

  public void throwErrorDeletedFunction(String oldFunctionName, String newFunctionName, String deletedDate) {
    def deleted = this.createDateFromParsedString(deletedDate)
    this.context.ansiColor('xterm') {
      this.context.println(
        "\u001b[35m /!\\ DELETION ERROR /!\\\n" +
        "“\033[0;4m\033[0;1m\u001b[35m${oldFunctionName}\u001b[0m\u001b[35m” is deleted since \033[0;4m\033[0;1m\u001b[35m" + this.formatToEnglishDate(deleted) + "\u001b[0m\u001b[35m, replaced by “\033[0;4m\033[0;1m\u001b[35m${newFunctionName}\u001b[0m\u001b[35m”" +
        "\u001B[0m"
      )

      throw new Exception("Function “${oldFunctionName}” is deleted since " + this.formatToEnglishDate(deleted) + ", replaced by “${newFunctionName}”")
    }
  }

  private Date createDateFromParsedString(String date, String format = "dd-MM-yyyy") {
    return new Date().parse(format, date)
  }

  private String formatToEnglishDate(Date date) {
    return date.format("EEEE, MMMM dd YYYY")
  }
}
