package stack

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class StackModuleTest extends AnyFlatSpec with ChiselScalatestTester {
  "StackModule" should "handle push, pop, and peek correctly" in {
    test(new StackModule(dataWidth = 8, len = 32)) { c =>
      // Initially all outputs should be 0
      c.io.out.expect(0.U)
      c.io.underflow.expect(false.B)
      c.io.overflow.expect(false.B)

      // Push some values
      for (i <- 0 until 5) {
        c.io.in.poke(i.U)
        c.io.push.poke(true.B)
        c.io.pop.poke(false.B)
        c.io.peek.poke(false.B)
        c.clock.step()
      }

      // Now test peek (should show last pushed = 4)
      c.io.push.poke(false.B)
      c.io.pop.poke(false.B)
      c.io.peek.poke(true.B)
      c.clock.step()
      c.io.out.expect(4.U)

      // Pop values and check output
      for (i <- 4 to 0 by -1) {
        c.io.pop.poke(true.B)
        c.io.peek.poke(false.B)
        c.clock.step()
        c.io.out.expect(i.U)
      }

      // Stack is now empty → underflow on extra pop
      c.io.pop.poke(true.B)
      c.clock.step()
      c.io.underflow.expect(true.B)

      // Push up to 32 items (max capacity)
      for (i <- 0 until 32) {
        c.io.in.poke((100 + i).U)
        c.io.push.poke(true.B)
        c.io.pop.poke(false.B)
        c.clock.step()
      }

      // Overflow when pushing again
      c.io.push.poke(true.B)
      c.io.in.poke(255.U)
      c.clock.step()
      c.io.overflow.expect(true.B)
    }
  }
}

