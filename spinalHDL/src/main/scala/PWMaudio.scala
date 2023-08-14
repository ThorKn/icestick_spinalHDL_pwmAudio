import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.fsm._

class PWMaudio extends Component{
    val io = new Bundle{
        val out_pwm_low = out Bool()
        val out_pwm_high = out Bool()
    }

    val pwmdriver_low = new PWMdriver(5)
    val pwmdriver_high = new PWMdriver(5)
    
    // **** Square Signal with 1024 resolution steps / freq 220 Hz (53 ticks/change)

    val counter1 = Counter(53)
    val counter2 = Counter(1024)
    val sqr_duty_cycle = UInt(10 bits)

    pwmdriver_low.io.dutyCycle := sqr_duty_cycle(0 to 4)
    pwmdriver_high.io.dutyCycle := sqr_duty_cycle(5 to 9)

    io.out_pwm_low := pwmdriver_low.io.pwmOutput
    io.out_pwm_high := pwmdriver_high.io.pwmOutput

    counter1.increment()
    when(counter1.willOverflow) {
        counter2.increment()
    }

    when (counter2.value < 512) {
        sqr_duty_cycle := 0
    }.otherwise{
        sqr_duty_cycle := 1023
    }
    
    // **** Sawtooth Signal with 1024 resolution steps / freq 220 Hz (53 ticks/change)

    // val counter1 = Counter(53) 
    // val counter2 = Counter(1024)

    // pwmdriver_low.io.dutyCycle := counter2.value(0 to 4)
    // pwmdriver_high.io.dutyCycle := counter2.value(5 to 9)

    // io.out_pwm_low := pwmdriver_low.io.pwmOutput
    // io.out_pwm_high := pwmdriver_high.io.pwmOutput

    // counter1.increment()
    // when(counter1.willOverflow) {
    //     counter2.increment()
    // } 
}

object PWMaudioMain{
    def main(args: Array[String]) {
        SpinalConfig(
            mode = Verilog,
            defaultClockDomainFrequency = FixedFrequency(12 MHz)
        ).generate(new PWMaudio)

        val spinalConfig = SpinalConfig(defaultClockDomainFrequency = FixedFrequency(12 MHz))

        SimConfig
            .withConfig(spinalConfig)
            .withWave
            .compile(new PWMaudio)
            .doSim{ pwmaudio =>
                pwmaudio.clockDomain.forkStimulus(2)
                var idx = 0
                while(idx < 220000){
                    pwmaudio.clockDomain.waitSampling()
                    idx += 1
                }
            }
    }
}