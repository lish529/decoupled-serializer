# Decoupled Adapter

A small adapter to adapt `DecoupledIO`s of different bit widths via serial consumption.

# Generating Verilog

Run `mill DecoupledAdapter.repl` and run:

```scala
println(circt.stage.ChiselStage.emitSystemVerilog(new decoupledadapter.Serializer(inputWidth=128, outputWidth=32)))
```

# Testing

```bash
mill DecoupledAdaptor.test
```
