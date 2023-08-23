import spinal.core._
import spinal.lib._

class PWMdriver(width: Int) extends Component {
  val io = new Bundle {
    val dutyCycle = in  UInt (width bits)  // Duty cycle as input (0 to 2^width-1)
    val pwm = out Bool()             // PWM output pin
  }

  val counter = Counter(width bits) 
  io.pwm := counter.value < io.dutyCycle
  counter.increment()

}