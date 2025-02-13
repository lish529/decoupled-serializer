解耦串行器
A small serializer to adapt DecoupledIOs of different bit widths via serial consumption.

产生verilog代码
Run mill DecoupledSerializer.repl and run:

println(circt.stage.ChiselStage.emitSystemVerilog(new decoupledserializer.Serializer(inputWidth=128, outputWidth=32)))
检验
mill DecoupledSerializer.test
