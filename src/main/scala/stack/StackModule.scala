package stack

import chisel3._
import chisel3.util._

class StackModule(val dataWidth: Int = 8, val len: Int = 32) extends Module {
  val io = IO(new Bundle {
    val in         = Input(UInt(dataWidth.W))    // Data to push
    val push       = Input(Bool())               // Push signal
    val pop        = Input(Bool())               // Pop signal
    val peek       = Input(Bool())               // Peek signal
    val out        = Output(UInt(dataWidth.W))   // Output data
    val underflow  = Output(Bool())              // Underflow flag
    val overflow   = Output(Bool())              // Overflow flag
  })

  val stackMem = Mem(len, UInt(dataWidth.W))
  val sp = RegInit(0.U(log2Ceil(len + 1).W))  // Stack pointer

  val outReg = RegInit(0.U(dataWidth.W))
  io.out := outReg

  io.underflow := false.B
  io.overflow := false.B

  when(io.push && !io.pop) {
    when(sp === len.U) {
      io.overflow := true.B
    } .otherwise {
      stackMem(sp) := io.in
      sp := sp + 1.U
    }
  } .elsewhen(io.pop && !io.push) {
    when(sp === 0.U) {
      io.underflow := true.B
    } .otherwise {
      sp := sp - 1.U
      outReg := stackMem(sp - 1.U)
    }
  } .elsewhen(io.peek) {
    when(sp === 0.U) {
      io.underflow := true.B
    } .otherwise {
      outReg := stackMem(sp - 1.U)
    }
  }
}
