FTMagsMIDetector : MIDetector{

	*new{|win,in=0,tag=0,args=nil|
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
		this.checkArg(\mult,1.0);
		this.setSynthArg([\mult]);
		name="FTMags";
		nBus=1024;
		bus=Buffer.alloc(Server.default, nBus); 
		bus.setn(nBus,0.dup(nBus));
	}

	loadSynthDef {	
		SynthDef(synthname,{|in=0,gate=1,bus,mult=1|
			var sig,chain;
			sig=InFeedback.ar(in);
			chain=FFT(LocalBuf(2048),sig);
			chain = PV_MagBuffer(chain, bus);
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addNormalizeButton();
		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);
		if(doPlot){this.addPlotter(xaxis:[20,22000,\exp].asSpec)};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}

	detect {|nets|
		bus.getn(0,nBus,{|val|
			if(doNormalize) {val=val.normalize};
			//do gui related stuff here
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
