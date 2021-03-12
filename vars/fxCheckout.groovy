import com.fxinnovation.deprecation.DeprecatedFunction
import com.fxinnovation.di.IOC
import com.fxinnovation.data.ScmInfo

def call(Map config = [:]) {
  registerServices()
  DeprecatedFunction deprecatedFunction = IOC.get(DeprecatedFunction.class.getName())
  return deprecatedFunction.execute({
    return IOC.get(ScmInfo.class.getName())
  }, 'fxCheckout', 'IOC component to get scmInfo: “ScmInfo scmInfo = IOC.get(ScmInfo.class.getName())”.', '01-03-2022')
}
