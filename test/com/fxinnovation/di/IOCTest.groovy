package test.com.fxinnovation.di

import org.junit.Test
import com.fxinnovation.di.IOC
import com.fxinnovation.di.IOCException
import static groovy.test.GroovyAssert.shouldFail

class IOCTest {
  @Test
  void testRegister() {
    IOC.register('ioc', {
      return new IOC()
    })

    assert IOC.resolve('ioc') != IOC.resolve('ioc')
  }

  @Test
  void testRegisterSingleton() {
    IOC.registerSingleton('iocSingleton', {
      return new IOC()
    })

    assert IOC.resolve('iocSingleton') == IOC.resolve('iocSingleton')
  }

  @Test
  void testResolveException() {
    def exception = shouldFail {
      IOC.resolve('notExistingClass')
    }

    assert exception instanceof IOCException
  }
}
