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

    assert IOC.get('ioc') != IOC.get('ioc')
  }

  @Test
  void testRegisterSingleton() {
    IOC.registerSingleton('iocSingleton', {
      return new IOC()
    })

    assert IOC.get('iocSingleton') == IOC.get('iocSingleton')
  }

  @Test
  void testGetException() {
    def exception = shouldFail {
      IOC.get('notExistingClass')
    }

    assert exception instanceof IOCException
  }
}
