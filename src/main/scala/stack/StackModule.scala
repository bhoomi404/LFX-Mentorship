package stack

import chisel3._
import chisel3.util._

class StackModule(val dataWidth: Int, val len: Int) extends Module {
  val io = IO(new Bundle {
    val in         = Input(UInt(32.W))
    val out        = Output(UInt(dataWidth.W))
    val underflow  = Output(Bool())
    val overflow   = Output(Bool())
    val isEmpty    = Output(Bool())
    val isFull     = Output(Bool())
    val popped     = Output(Bool())
    val peeked     = Output(Bool())
  })

  val PUSH_OPCODE = "b0100111".U(7.W)
  val POP_OPCODE  = "b1000011".U(7.W)
  val PEEK_OPCODE = "b1000000".U(7.W)

  val stack = Reg(Vec(len, UInt(dataWidth.W)))
  val sp = RegInit(0.U(log2Ceil(len + 1).W))

  val opcode = io.in(6, 0)
  val imm    = io.in(31, 7)
  val dataToPush = imm.zext().asUInt()(dataWidth - 1, 0)

  io.out := 0.U
  io.underflow := false.B
  io.overflow := false.B
  io.isEmpty := sp === 0.U
  io.isFull := sp === len.U
  io.popped := false.B
  io.peeked := false.B

  when(opcode === PUSH_OPCODE) {
    when(io.isFull) {
      io.overflow := true.B
    } .otherwise {
      stack(sp) := dataToPush
      sp := sp + 1.U
    }
  } .elsewhen(opcode === POP_OPCODE) {
    when(io.isEmpty) {
      io.underflow := true.B
    } .otherwise {
      sp := sp - 1.U
      io.out := stack(sp - 1.U)
      io.popped := true.B
    }
  } .elsewhen(opcode === PEEK_OPCODE) {
    when(io.isEmpty) {
      io.underflow := true.B
    } .otherwise {
      io.out := stack(sp - 1.U)
      io.peeked := true.B
    }
  }
}
