import com.fxinnovation.data.ScmInfo
import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.di.IOC

def call (Map config = [:]){
  registerServices()

  DeprecatedFunction deprecatedFunction = IOC.get(DeprecatedFunction.class.getName())

  return deprecatedFunction.execute({
      return IOC.get(ScmInfo.class.getName())
    },
    'fxCheckoutTag',
    'IOC component to get scmInfo: “ScmInfo scmInfo = IOC.get(ScmInfo.class.getName())”. Also pass config.checkoutTag to checkout a specific tag.',
    '01-03-2022'
  )
}
