import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.fsm._

import java.util.Arrays

class PWMaudio extends Component{
    val io = new Bundle{
        val pwm_1 = out Bool()
        val pwm_2 = out Bool()
        val frequency = in Bits(12 bits)
        val note_length = in Bits(3 bits)
        val adsr_switch = in Bool()
        val loop = in Bool()
        val trigger = in Bool()
        val adsr_choice = in Bits(3 bits)
    }

    val adsr1 = UInt(24 bit)
    val adsr2 = UInt(24 bit)
    val adsr3 = UInt(24 bit)
    val adsr4 = UInt(24 bit)

    switch(U(io.adsr_choice)){
        is(0) {
            adsr1 := 50
            adsr2 := 100
            adsr3 := 500
            adsr4 := 100
        }
        is(1) {
            adsr1 := 50
            adsr2 := 500
            adsr3 := 500
            adsr4 := 100
        }
        is(2) {
            adsr1 := 10
            adsr2 := 1500
            adsr3 := 1000
            adsr4 := 400
        }
        is(3) {
            adsr1 := 10
            adsr2 := 2000
            adsr3 := 1500
            adsr4 := 400
        }
        is(4) {
            adsr1 := 1000
            adsr2 := 100
            adsr3 := 2000
            adsr4 := 400
        }
        is(5) {
            adsr1 := 1000
            adsr2 := 2000
            adsr3 := 2000
            adsr4 := 1000
        }
        is(6) {
            adsr1 := 5000
            adsr2 := 1000
            adsr3 := 500
            adsr4 := 400
        }
        is(7) {
            adsr1 := 5000
            adsr2 := 5000
            adsr3 := 1000
            adsr4 := 10000
        }
    }

    val pwm_ctrl = new PWMctrl(8)
    val note_length = U(io.note_length)

    io.pwm_1 := pwm_ctrl.io.pwm_1
    io.pwm_2 := pwm_ctrl.io.pwm_2

    pwm_ctrl.io.freq := U(io.frequency)

    val fsm_adsr = new StateMachine{
        val counter = Reg(UInt(24 bits)) init (0)
        val level_adsr = Reg(UInt(8 bits)) init (0)
        when(io.adsr_switch) {
            pwm_ctrl.io.level := level_adsr
        } otherwise {
            pwm_ctrl.io.level := 255
        }

        val stateEntry : State = new State with EntryPoint{
            whenIsActive {
                when(io.loop) {
                    goto(stateAttack)
                } otherwise {
                    when(!io.trigger) {
                        goto(stateAttack)
                    }
                }
            }
        }
        val stateAttack : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                when(counter >= adsr1 * (note_length * 2 )){
                    level_adsr := level_adsr + 1
                    counter := 0
                }
                when(level_adsr === 255) {
                    goto(stateDelay)
                }
            }
        }
        val stateDelay : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                when(counter >= adsr2 * note_length * 2){
                    level_adsr := level_adsr - 1
                    counter := 0
                }
                when(level_adsr === 127) {
                    goto(stateSustain)
                }
            }
        }
        val stateSustain : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                level_adsr := 127
                when(counter >= adsr3 * note_length * 2){
                    goto(stateRelease)
                }
            }
        }
        val stateRelease : State = new State{
            onEntry(counter := 0)
            whenIsActive {
                counter := counter + 1
                when(counter >= adsr4 * note_length * 2){
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

        // SimConfig
        //     .withConfig(spinalConfig)
        //     .withWave
        //     .compile(new PWMaudio)
        //     .doSim{ pwmaudio =>
        //         pwmaudio.clockDomain.forkStimulus(2)
        //         var idx = 0
        //         // pwmaudio.io.frequency = 255
        //         while(idx < 220000){
        //             pwmaudio.clockDomain.waitSampling()
        //             idx += 1
        //         }
        //     }
    }
}