package com.fxinnovation.data

class UserInput {
  private groovy.lang.Script context

  private String name = 'Undefined'
  private Object defaultValue = ''
  private String description = ''
  private Class<?> type = CharSequence

  UserInput(groovy.lang.Script context) {
    this.context = context
  }

  String getName() {
    return name
  }

  UserInput setName(String name) {
    if (3 > name.length()) {
      this.context.error("Invalid name (“${name}”) for user input. To make sure the name is meaningful, you are required to specify at least 3 characters.")
    }
    this.name = name
    return this
  }

  Object getDefaultValue() {
    return this.defaultValue
  }

  UserInput setDefaultValue(Object defaultValue) {
    if (!(defaultValue instanceof String)) {
      this.context.println("Warning: you passed an default value that is not a String (${defaultValue.getClass()}). This might be unwanted and leads to unstable results.")
    }

    this.defaultValue = defaultValue
    return this
  }

  String getDescription() {
    return this.description
  }

  UserInput setDescription(String description) {
    this.description = description
    return this
  }

  Class<?> getType() {
    return this.type
  }

  UserInput setType(Class<?> type) {
    this.type = type
    return this
  }
}
