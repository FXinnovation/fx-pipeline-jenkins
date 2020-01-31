import com.fxinnovation.io.Debugger

@NonCPS
def call(Object message) {
  def debbuger = new Debugger(this)
  debbuger.printDebug(message)
}
