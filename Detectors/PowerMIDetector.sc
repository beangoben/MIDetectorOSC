PowerMIDetector : MIDetector{
	
	*new{|win,in=0,args=nil|
		^super.newCopyArgs(win,in,args).init();	
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

		name="Power";
		nBus=this.getArgValue(\nbands);
		bus=Bus.control(Server.default,nBus);	
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
		
		controls.put(\show,
			MultiSliderView(win, Rect(0, 0, 256,50))
			.value_(0.dup(nBus))
			.size_(nBus)
			.drawLines_(true)
			.drawRects_(false)
			.indexThumbSize_(256/nBus)
		);	

		controls.put(\mult,
			EZSlider(win,225@18,"Mult",[0.01,100,\exp,0.01].asSpec,
			{|ez|synth.set(\mult,ez.value) }
			,this.getArgValue(\mult),false,labelWidth:30,numberWidth:25)
		);

		controls.put(\showsum,EZNumber(win,125@18,"Sum:"));

		win.setInnerExtent(win.bounds.width,win.bounds.height+(24*3));
	}
	
	detect {|net,tag|
		bus.getn(nBus,{|val|
			val=val.max(0).min(1); //limit range
			if(verbose){format("% :  % ",name,val).post};
			{
				controls[\show].value_(val);
				controls[\showsum].value_(val.sum);
				}.defer;
			net.sendMsg(oscstr,tag,nBus,val.asFloat.round(0.01));
			});
		}	
	
}