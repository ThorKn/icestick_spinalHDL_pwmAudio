TOPMOD  := PWMaudio

.PHONY: clean prog
clean:
	rm -rf $(TOPMOD).json $(TOPMOD).asc $(TOPMOD).bin

$(TOPMOD).bin: $(TOPMOD).asc
	icepack $(TOPMOD).asc $(TOPMOD).bin 

$(TOPMOD).asc: $(TOPMOD).json
	nextpnr-ice40 --hx1k --json $(TOPMOD).json --pcf icestick.pcf --asc $(TOPMOD).asc

$(TOPMOD).json: $(TOPMOD).v
	yosys -p "synth_ice40 -top $(TOPMOD) -json $(TOPMOD).json" $(TOPMOD).v

prog: $(TOPMOD).bin
	sudo iceprog $(TOPMOD).bin
