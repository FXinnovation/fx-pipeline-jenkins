import com.fxinnovation.io.Debugger

import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def call(Object message) {
  def debbuger = serviceContainer.get(Debugger.class.getClass())
  debbuger.printDebug(message)
}
