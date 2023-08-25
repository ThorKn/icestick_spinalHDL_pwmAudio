import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.fsm._

import java.util.Arrays

class PWMaudio extends Component{
    val io = new Bundle{
        val pwm_low = out Bool()
        val pwm_high = out Bool()
    }

    var list = List(200 , 400, 425000, 600)
    var adsr_table = Array[Int](list: _*)

    val pwm_ctrl = new PWMctrl(5)
    val note_length = U(4, 4 bits)
    // val rom_adsr = Mem(UInt(20 bits), initialContent = list)

    io.pwm_low  := pwm_ctrl.io.pwm_low
    io.pwm_high := pwm_ctrl.io.pwm_high

    // val sweep_counter = Counter (11718)
    // val level_sweep = Reg(U(0, 10 bits))

    pwm_ctrl.io.freq        := 26
    // pwm_ctrl.io.level       := level_sweep
    pwm_ctrl.io.waveform    := True

    // sweep_counter.increment()
    // when(sweep_counter.willOverflow) {
    //     level_sweep := level_sweep - 1
    // }

    val fsm_adsr = new StateMachine{
        val counter = Reg(UInt(24 bits)) init (0)
        val level_adsr = Reg(UInt(10 bits)) init (0)
        pwm_ctrl.io.level := level_adsr

        val stateEntry : State = new State with EntryPoint{
            whenIsActive (goto(stateAttack))
        }
        val stateAttack : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                when(counter === adsr_table(0) * note_length){
                    level_adsr := level_adsr + 1
                    counter := 0
                }
                when(level_adsr === 1000) {
                    goto(stateDelay)
                }
            }
        }
        val stateDelay : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                when(counter === adsr_table(1) * note_length){
                    level_adsr := level_adsr - 1
                    counter := 0
                }
                when(level_adsr === 500) {
                    goto(stateSustain)
                }
            }
        }
        val stateSustain : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                level_adsr := 500
                when(counter === adsr_table(2) * note_length){
                    goto(stateRelease)
                }
            }
        }
        val stateRelease : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                when(counter === adsr_table(3) * note_length){
                    level_adsr := level_adsr - 1
                    counter := 0
                }
                when(level_adsr === 0) {
                    goto(stateEntry)
                }
            }
        }
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
                while(idx < 220000){
                    pwmaudio.clockDomain.waitSampling()
                    idx += 1
                }
            }
    }
}