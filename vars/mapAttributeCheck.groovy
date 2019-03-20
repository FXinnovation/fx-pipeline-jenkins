def call(Map mapToCheck, String keyToCheck, Class<?> expectedValueType, defaultValue, String keyUndefinedErrorMessage = '') {
  if ('' == keyToCheck) {
    error('Cannot check an empty key. Make sur the “keyToCheck” argument has a proper value.')
  }

  if (!mapToCheck.containsKey(keyToCheck) && '' != keyUndefinedErrorMessage) {
    error(keyUndefinedErrorMessage)
  }

  if (!mapToCheck.containsKey(keyToCheck)) {
    mapToCheck[keyToCheck] = defaultValue
  }

  if (!(expectedValueType.isInstance(mapToCheck[keyToCheck]))) {
    error("The type of the value for key “${keyToCheck}” is expected to be “${expectedValueType}”, given: “${mapToCheck[keyToCheck].getClass()}”")
  }
}
