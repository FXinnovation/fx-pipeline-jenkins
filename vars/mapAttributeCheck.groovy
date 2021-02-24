def call(Map mapToCheck, String keyToCheck, Class<?> expectedValueType, defaultValue, String keyUndefinedErrorMessage = '') {
  if ('' == keyToCheck) {
    error('Cannot check a Map’s value with an empty key. Make sure the “keyToCheck” argument has a proper value.')
  }

  if (!mapToCheck.containsKey(keyToCheck) && '' != keyUndefinedErrorMessage) {
    error(keyUndefinedErrorMessage)
  }

  if (!mapToCheck.containsKey(keyToCheck) || NullObject.isInstance(mapToCheck[keyToCheck])) {
    mapToCheck[keyToCheck] = defaultValue
  }

  print(keyToCheck)
  print(mapToCheck)
  print(mapToCheck.containsKey(keyToCheck))
  print(defaultValue)
  print(mapToCheck[keyToCheck])
  print(mapToCheck[keyToCheck].getClass())

  if (!(expectedValueType.isInstance(mapToCheck[keyToCheck]))) {
    error("The type of the value for key “${keyToCheck}” is expected to be “${expectedValueType}”, given: “${mapToCheck[keyToCheck].getClass()}”")
  }
}
