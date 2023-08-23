import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.fsm._

class PWMaudio extends Component{
    val io = new Bundle{
        val pwm_low = out Bool()
        val pwm_high = out Bool()
    }

    val pwm_ctrl = new PWMctrl(5)

    io.pwm_low  := pwm_ctrl.io.pwm_low
    io.pwm_high := pwm_ctrl.io.pwm_high

    val sweep_counter = Counter (3000)
    val level_sweep = Reg(U(0, 10 bits))

    pwm_ctrl.io.freq        := 53
    pwm_ctrl.io.level       := level_sweep
    pwm_ctrl.io.waveform    := True

    sweep_counter.increment()
    when(sweep_counter.willOverflow) {
        level_sweep := level_sweep + 1
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

        // val spinalConfig = SpinalConfig(defaultClockDomainFrequency = FixedFrequency(12 MHz))

        // SimConfig
        //     .withConfig(spinalConfig)
        //     .withWave
        //     .compile(new PWMaudio)
        //     .doSim{ pwmaudio =>
        //         pwmaudio.clockDomain.forkStimulus(2)
        //         var idx = 0
        //         while(idx < 220000){
        //             pwmaudio.clockDomain.waitSampling()
        //             idx += 1
        //         }
        //     }
    }
}