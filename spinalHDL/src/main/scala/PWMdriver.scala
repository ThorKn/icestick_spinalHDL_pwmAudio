import spinal.core._
import spinal.lib._

class PWMdriver(width: Int) extends Component {
  val io = new Bundle {
    val dutyCycle = in  UInt (width bits)  // Duty cycle as input (0 to 2^width-1)
    val pwmOutput = out Bool()             // PWM output pin
  }

  val counter = Counter(width bits) 

  counter.increment()

  // Generate PWM output based on duty cycle and frequency
  io.pwmOutput := counter.value < io.dutyCycle

}
