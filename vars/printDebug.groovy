import com.fxinnovation.di.IOC
import com.fxinnovation.io.Debugger

def call(Object message) {
  def Debugger debbuger = IOC.get(Debugger.class.getName())
//  def Debugger debbuger = new Debugger(this)
//
//  println(debbuger.getName())
  debbuger.printDebug(message)
}
