import spinal.core._
import spinal.lib._

class PWMctrl(width: Int) extends Component {
    val io = new Bundle {
        val freq        = in  UInt (12 bits)    // Frequency counter (divider)
        val level       = in  UInt (8 bits)     // Amplitude level
        val pwm_1       = out Bool()            // PWM 1 output pin (square)
        val pwm_2       = out Bool()            // PWM 2 output pin (ramp)
    }

    val freq_counter = Counter (12 bits)
    val pwm_steps_counter = Counter (width bits)

    // PWM 1 is for square waves
    val pwmdriver_1 = new PWMdriver(width)
    val duty_cycle_1   = UInt (width bits)
    io.pwm_1  := pwmdriver_1.io.pwm
    pwmdriver_1.io.dutyCycle := duty_cycle_1

    // Count to io.freq before the next pwm_step.
    freq_counter.increment()
    when(freq_counter.value === io.freq){
        freq_counter.clear()
        pwm_steps_counter.increment()
    }

    // **** Square Wave Signal on PWM 1
    when(pwm_steps_counter.value < 127) {
        duty_cycle_1 := 0
    }.otherwise {
        duty_cycle_1 := io.level
    }

    // PWM 2 is for ramp waves
    val pwmdriver_2 = new PWMdriver(width)
    io.pwm_2  := pwmdriver_2.io.pwm
    pwmdriver_2.io.dutyCycle := pwm_steps_counter

}
