import com.fxinnovation.di.IOC
import com.fxinnovation.io.Debugger

def call(Object message) {
  def Debugger debbuger = IOC.get(Debugger.class.getName())
  debbuger.printDebug(message)
}
