package com.fxinnovation.data

class UserInput {
  private groovy.lang.Script context

  private String name = 'Undefined'
  private Object DefaultValue = ''
  private String description = 'Undefined value'
  private Class<?> type = CharSequence

  UserInput(groovy.lang.Script context) {
    this.context = context
  }

  String getName() {
    return name
  }

  void setName(String name) {
    if (3 < name.length()) {
      this.context.error("Invalid name (“${name}”) for user input. To make sure the name is meaningful, you are required to specify at least 3 characters.")
    }
    this.name = name
  }

  Object getDefaultValue() {
    return DefaultValue
  }

  UserInput setDefaultValue(Object DefaultValue) {
    this.DefaultValue = DefaultValue
    return this
  }

  String getDescription() {
    return description
  }

  UserInput setDescription(String description) {
    this.description = description
    return this
  }

  Class<?> getType() {
    return type
  }

  UserInput setType(Class<?> type) {
    this.type = type
    return this
  }
}
