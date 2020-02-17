package com.fxinnovation.di

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

  static get(String className, ...args) {
    if (!this.isRegistered(className) && !this.isRegisteredAsSingleton(className)) {
      throw new IOCException('No class registered with the name “'+className+'”.')
    }

    if (this.isRegisteredAsSingleton(className)) {
      if(!this.isInstanciated(className)) {
        // This is because CPS pipelines does not handle spread syntax
        switch(args.size()) {
          case 0:
            this.instances[className] = this.registryForSingleton[className]()
            break
          case 1:
            this.instances[className] = this.registryForSingleton[className](args[0])
            break
          case 2:
            this.instances[className] = this.registryForSingleton[className](args[0], args[1])
            break
          case 3:
            this.instances[className] = this.registryForSingleton[className](args[0], args[1], args[2])
            break
          default:
            new Exception('Cannot pass more that 3 arguments to get class “'+className+'”. This is because CPS pipeline does not handle spread. Lower your number of arguments or add more argument to the IOC class.')
        }
      }

      return this.instances[className]
    }

    // This is because CPS pipelines does not handle spread syntax
    switch(args.size()) {
      case 0:
        return this.registry[className]()
        break
      case 1:
        return this.registry[className](args[0])
        break
      case 2:
        return this.registry[className](args[0], args[1])
        break
      case 3:
        return this.registry[className](args[0], args[1], args[2])
        break
      default:
        new Exception('Cannot pass more that 3 arguments to get class “'+className+'”. This is because CPS pipeline does not handle spread. Lower your number of arguments or add more argument to the IOC class.')
    }
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
