import spinal.core._
import spinal.lib._

class PWMctrl(width: Int) extends Component {
  val io = new Bundle {
    val tone     = in  UInt (6 bits)    // Max 2^6 = 64 tones (>5 octaves)
    val waveform = in  Bool()           // Square or Sawtooth waves
    val pwm_low  = out Bool()           // PWM low output pin
    val pwm_high = out Bool()           // PWM high output pin
  }


    val pwmdriver_low = new PWMdriver(5)
    val pwmdriver_high = new PWMdriver(5)


}
