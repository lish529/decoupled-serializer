package decoupledserializer

import chisel3._
import chisel3.util.Decoupled
import chisel3.experimental.BundleLiterals._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

// Default svsim flow in Chisel 6+ doesn't support VCD dumping.
// See VCDHackedEphemeralSimulator.scala for details
//import chisel3.simulator.EphemeralSimulator._
import chisel3.simulator.VCDHackedEphemeralSimulator._

class SerializerSpec extends AnyFlatSpec with Matchers {

  "Serializer" should "do a basic serialization" in {
    simulate(new Serializer(inputWidth = 16, outputWidth = 4)) { dut =>
      dut.output.ready.poke(false.B)
      dut.input.valid.poke(false.B)

      // reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()

      // Feed in input
      assert(dut.input.ready.peek().litToBoolean)
      dut.input.valid.poke(true.B)
      dut.input.bits.poke("hdcba".U)
      dut.clock.step()
      dut.input.valid.poke(false.B)

      // Read outputs
      dut.output.ready.poke(true.B)
      assert(dut.output.bits.peekValue().asBigInt == 0xa)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xb)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xc)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xd)
      dut.clock.step()
      dut.output.ready.poke(false.B)
    }
  }

  it should "serialize multiple values" in {
    simulate(new Serializer(inputWidth = 16, outputWidth = 4)) { dut =>
      dut.output.ready.poke(false.B)
      dut.input.valid.poke(false.B)

      // reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()

      // Feed in input
      assert(dut.input.ready.peek().litToBoolean)
      dut.input.valid.poke(true.B)
      dut.input.bits.poke("hdcba".U)
      dut.clock.step()
      dut.input.valid.poke(false.B)

      // Read outputs
      dut.output.ready.poke(true.B)
      assert(dut.output.bits.peekValue().asBigInt == 0xa)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xb)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xc)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xd)
      dut.clock.step()
      dut.output.ready.poke(false.B)

      // Pass some time
      dut.clock.step()
      dut.clock.step()

      // Feed in input
      assert(dut.input.ready.peek().litToBoolean)
      dut.input.valid.poke(true.B)
      dut.input.bits.poke("h1234".U)
      dut.clock.step()
      dut.input.valid.poke(false.B)

      // Read outputs
      dut.output.ready.poke(true.B)
      assert(dut.output.bits.peekValue().asBigInt == 4)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 3)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 2)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 1)
      dut.clock.step()
      dut.output.ready.poke(false.B)
    }
  }
}

class DeumxerTestbench extends Module {
  val input = IO(Flipped(Decoupled(UInt(16.W))))
  val output = IO(Decoupled(UInt(4.W)))
  val mux = Module(new Demuxer(width = 16))
  val serializer = Module(new Serializer(inputWidth = 16, outputWidth = 4))
  mux.in <> input
  mux.out0 <> serializer.input
  mux.out1.ready := false.B // tie off unused port
  output <> serializer.output
}

class DeumxerSpec extends AnyFlatSpec with Matchers {

  "Deumxer" should "work with a serializer" in {
    simulate(new DeumxerTestbench) { dut =>
      dut.output.ready.poke(false.B)
      dut.input.valid.poke(false.B)

      // reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()

      // Feed in input
      assert(dut.input.ready.peek().litToBoolean)
      dut.input.valid.poke(true.B)
      dut.input.bits.poke("hdcba".U)
      dut.clock.step()
      dut.input.valid.poke(false.B)

      // Read outputs
      dut.output.ready.poke(true.B)
      assert(dut.output.bits.peekValue().asBigInt == 0xa)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xb)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xc)
      dut.clock.step()
      assert(dut.output.bits.peekValue().asBigInt == 0xd)
      dut.clock.step()
      dut.output.ready.poke(false.B)
    }
  }
}
