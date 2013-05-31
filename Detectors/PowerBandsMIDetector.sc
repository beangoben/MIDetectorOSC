PowerBandsMIDetector : MIDetector{
	
	*new{|win,in=0,tag=0,args|
		^super.newCopyArgs(win,in,tag,args).init();	
	}	
	
	init {
		if(args.isNil,{args=()},{var tmp=();tmp.putPairs(args);args=tmp;});
		this.initValues();
		super.init();
		this.loadSynthDef();
		super.makeGenericGui();
		this.makeSpecificGui();
	}

	initValues {
		var cutfreqs,nbands;
		if(args[\nbands].isNil){
			if(args[\cutfreqs].isNil){nbands=32;}{nbands=args[\cutfreqs].size}
		}{nbands=32;};
		this.checkArg(\nbands,nbands);
		this.checkArg(\mult,1.0);
		this.checkArg(\spec,\freq.asSpec);
		this.checkArg(\scalemode,2);
		nBus=args[\nbands];
		cutfreqs=Array.fill(nBus-1,{|i| args[\spec].map(i/(nBus-1))});
		this.checkArg(\cutfreqs,cutfreqs);
		this.setSynthArg([\mult]);

		name="PowerBands";
		bus=Bus.control(Server.default,nBus);	
		bus.setn(0.dup(nBus));

	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,bus,mult=1|
			var buffer,sig,powers,chain;
			buffer=LocalBuf(2048);
			sig=InFeedback.ar(in);
			chain=FFT(buffer,sig);
			powers = FFTSubbandPower.kr(chain,args[\cutfreqs],1,args[\scalemode]);
			Out.kr(bus, powers*mult);
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addNormalizeButton();
		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);
		if(doPlot){this.addPlotter(xaxis:args[\spec]);controls[\plot].plotMode_(\steps);};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}

	detect {|nets|

		bus.getn(nBus,{|val|
			if(doNormalize) {val=val.normalize};
			{
			if(doStats){this.updateStats(val)};
			if(verbose){format("% :  % ",name,val).postln};
			if(doPlot){
				controls[\plot].setValue(val,findSpecs: false);
				controls[\plot].calcSpecs;
			};
			}.defer;
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,nBus,val) });
		});
	}	
	
}