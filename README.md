# icestick_spinalHDL_template
A template for starting a spinalHDL project targeted at the Lattice icestick FPGA.

### Create Verilog from spinalHDL

```
cd spinalHDL
sbt "runMain MyImplementation"
cd ..
```
### Copy Verilog over into the icestick folder
```
cp spinalHDL/MyImplementation.v icestick/
```

### Create bitstream from Verilog and flash to icestick
```
cd icestick
make prog
```

### What this example does
Pin PMOD1 is an input. 
Pin LED D1 (99) is an output.
Input and output gets connected via an inverter.  
