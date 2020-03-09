package com.fxinnovation.service.standard

import org.junit.Test

class ServiceRegistererTest {
  @Test
  void testConstructor() {
    new ServiceRegisterer()
  }

  @Test
  void testRegisterAll() {
    def serviceRegisterer = new ServiceRegisterer()

    serviceRegisterer.registerAllClasses(null)
  }
}
