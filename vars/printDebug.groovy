import com.fxinnovation.io.Debugger

import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def call(Object message) {
  def debbuger = new Debugger(this)
  debbuger.printDebug(message)
}
