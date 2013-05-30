PowerBandsMIDetector : MIDetector{
	
	*new{|win,in=0,tag=0,args=nil|
		^super.newCopyArgs(win,in,tag,args).init();	
	}	
	
	init {
		this.initValues();
		super.init();
		this.loadSynthDef();
		super.makeGenericGui();
		this.makeSpecificGui();
	}

	initValues {
		//create default values if not present
		if(args.isNil,{args=[]});
		this.checkArg(\nbands,32);
		this.checkArg(\minfreq,220);
		this.checkArg(\maxfreq,14080);
		this.checkArg(\mult,1.0);
		this.checkArg(\scalemode,2);

		name="PowerBands";
		nBus=this.getArgValue(\nbands);
		bus=Bus.control(Server.default,nBus);	
		bus.setn(0.dup(nBus));
		value=0;
	}

	loadSynthDef {
		var cutfreqs,maxfreq,minfreq,stepsize,scalemode;
		//create frequencies for subbands dependinf on min and max
		maxfreq=this.getArgValue(\maxfreq);
		minfreq=this.getArgValue(\minfreq);
		stepsize=(log2(maxfreq)-log2(minfreq))/(nBus-2);
		cutfreqs=Array.fill(nBus-1,{ arg i;
			2**(stepsize*i + log2(minfreq))
		});
		scalemode=this.getArgValue(\scalemode);

		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,bus,mult=1|
			var buffer,sig,powers,chain;
			buffer=LocalBuf(2048);
			sig=InFeedback.ar(in);
			chain=FFT(buffer,sig);
			powers = FFTSubbandPower.kr(chain,cutfreqs,1,scalemode);
			Out.kr(bus, powers*mult);
		}).load(Server.default);
	}

	makeSpecificGui {
		
		this.showMultiSlider();
		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);
		controls.put(\showsum,EZNumber(win,125@18,"Sum:"));

		win.setInnerExtent(win.bounds.width,win.bounds.height+(24*3));
	}
	
	detect {|net|
		bus.getn(nBus,{|val|
			val=val.max(0).min(1); //limit range
			if(verbose){format("% :  % ",name,val).postln};
			{
				controls[\show].value_(val);
				controls[\showsum].value_(val.sum);
				}.defer;
			net.sendMsg(oscstr,tag,nBus,val.asFloat.round(0.01));
			});
		}	
	
}