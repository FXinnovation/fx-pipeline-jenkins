package com.fxinnovation.di

//@NonCPS
class IOC {
  protected static registry = [:]
  protected static registryForSingleton = [:]
  protected static instances = [:]

  static void register(String className, Closure resolve) {
    this.registry[className] = resolve
  }

  static void registerSingleton(String className, Closure resolve) {
    this.registryForSingleton[className] = resolve
  }

  static resolve(String className, ...args) {
    if (!this.isRegistered(className) && !this.isRegisteredAsSingleton(className)) {
      throw new IOCException('No class registered with the name “'+className+'”.')
    }

    if (this.isRegisteredAsSingleton(className)) {
      if(!this.isInstanciated(className)) {
        this.instances[className] = this.registryForSingleton[className](*args)
      }

      return this.instances[className]
    }

    return this.registry[className](*args)
  }

  static Boolean isRegistered(String name) {
    return this.registry.containsKey(name)
  }

  static Boolean isRegisteredAsSingleton(String name) {
    return this.registryForSingleton.containsKey(name)
  }

  static Boolean isInstanciated(String name) {
    return this.instances.containsKey(name)
  }
}
