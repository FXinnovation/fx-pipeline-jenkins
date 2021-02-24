import org.codehaus.groovy.runtime.NullObject

def call(Map mapToCheck, String keyToCheck, Class<?> expectedValueType, defaultValue, String keyUndefinedErrorMessage = '') {
  if ('' == keyToCheck) {
    error('Cannot check a Map’s value with an empty key. Make sure the “keyToCheck” argument has a proper value.')
  }

  if (!mapToCheck.containsKey(keyToCheck) && '' != keyUndefinedErrorMessage) {
    error(keyUndefinedErrorMessage)
  }

  if (!(mapToCheck.containsKey(keyToCheck)) || null == mapToCheck[keyToCheck]) {
    mapToCheck[keyToCheck] = defaultValue
  }

  if (!(expectedValueType.isInstance(mapToCheck[keyToCheck]))) {
    error("The type of the value for key “${keyToCheck}” is expected to be “${expectedValueType}”, given: “${mapToCheck[keyToCheck].getClass()}”")
  }
}
