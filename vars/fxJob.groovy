import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.di.IOC

def call(Map closures = [:], List propertiesConfig = [], Map config = [:]) {

  def deprecatedFunction = IOC.get(DeprecatedFunction.class.getName())
  deprecatedFunction.execute(legacyFunction, 'fxJob', 'standardJob', '07-01-2022')

  standardJob(
    closureHelper.getClosures(),
    [
      disableConcurrentBuilds()
    ],
    config
  )
}
