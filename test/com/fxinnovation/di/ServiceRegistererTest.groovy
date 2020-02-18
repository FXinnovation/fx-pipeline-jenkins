package com.fxinnovation.di

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
