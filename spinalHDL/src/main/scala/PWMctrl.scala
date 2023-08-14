import spinal.core._
import spinal.lib._

class PWMctrl(width: Int) extends Component {
    val io = new Bundle {
        val freq     = in  UInt (8 bits)    // Frequency counter (divider)
        val level    = in  UInt (10 bits)   // Amplitude level
        val waveform = in  Bool()           // Square or Sawtooth waves
        val pwm_low  = out Bool()           // PWM lower bits output pin
        val pwm_high = out Bool()           // PWM higher bits output pin
    }

    val pwmdriver_low  = new PWMdriver(width)
    val pwmdriver_high = new PWMdriver(width)

    io.pwm_low  := pwmdriver_low.io.pwm
    io.pwm_high := pwmdriver_high.io.pwm

    val duty_cycle   = UInt ((width*2) bits)
    val freq_counter = Counter (8 bits)
    val wave_counter = Counter ((width*2) bits)

    pwmdriver_low.io.dutyCycle  := duty_cycle(0 to (width-1))
    pwmdriver_high.io.dutyCycle := duty_cycle(width to (width*2-1))

    freq_counter.increment()
    when(freq_counter.value === io.freq){
        freq_counter.clear()
        wave_counter.increment()
    }

    when(wave_counter.value < 512) {
        duty_cycle := 0
    }.otherwise {
        duty_cycle := io.level
    }
}
