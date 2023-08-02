import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.fsm._

class PWMaudio extends Component{
    val io = new Bundle{
        val out1 = out Bool()
    }

    val pwmdriver = new PWMdriver(8)
    val counter1 = Counter(8 bits) 
    val counter2 = Counter(8 bits) 
    counter1.increment()

    pwmdriver.io.dutyCycle := counter2.value
    io.out1 := pwmdriver.io.pwmOutput

    when(counter1.willOverflow) {
        counter2.increment()
    }
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
                while(idx < 1000000){
                    pwmaudio.clockDomain.waitSampling()
                    idx += 1
                }
            }
    }
}