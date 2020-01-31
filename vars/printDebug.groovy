import com.fxinnovation.io.Debugger

@NonCPS
def call(Object message) {
  def debbuger = new Debugger(this)
  debbuger.print(message)
}
