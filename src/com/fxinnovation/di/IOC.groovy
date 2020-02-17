package com.fxinnovation.di

import com.cloudbees.groovy.cps.NonCPS

class IOC {
  protected static registry = [:]
  protected static registryForSingleton = [:]
  protected static instances = [:]

  @NonCPS
  static void register(String className, Closure resolve) {
    this.registry[className] = resolve
  }

  @NonCPS
  static void registerSingleton(String className, Closure resolve) {
    this.registryForSingleton[className] = resolve
  }

  @NonCPS
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

  @NonCPS
  static Boolean isRegistered(String name) {
    return this.registry.containsKey(name)
  }

  @NonCPS
  static Boolean isRegisteredAsSingleton(String name) {
    return this.registryForSingleton.containsKey(name)
  }

  @NonCPS
  static Boolean isInstanciated(String name) {
    return this.instances.containsKey(name)
  }
}
