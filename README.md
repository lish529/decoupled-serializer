# Decoupled Serializer

A small serializer to adapt `DecoupledIO`s of different bit widths via serial consumption.

# Generating Verilog

Run `mill DecoupledSerializer.repl` and run:

```scala
println(circt.stage.ChiselStage.emitSystemVerilog(new decoupledserializer.Serializer(inputWidth=128, outputWidth=32)))
```

# Testing

```bash
mill DecoupledSerializer.test
```
