FlatBandsMIDetector : MIDetector{
	
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
		this.checkArg(\nbands,3);
		this.checkArg(\minfreq,220);
		this.checkArg(\maxfreq,14080);
		name="FlatBands";
		nBus=this.getArgValue(\nbands);
		bus=Bus.control(Server.default,nBus);
		[bus,nBus].postln;
		value=0;
	}

	loadSynthDef {
		var cutfreqs,maxfreq,minfreq,stepsize;
		//create frequencies for subbands depending on min and max
		maxfreq=this.getArgValue(\maxfreq);
		minfreq=this.getArgValue(\minfreq);
		stepsize=(log2(maxfreq)-log2(minfreq))/(nBus-2);
		cutfreqs=Array.fill(nBus-1,{ arg i;
			2**(stepsize*i + log2(minfreq))
		});

		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,bus|
			var sig,flats,chain;
	        sig=InFeedback.ar(in);
			chain=FFT(LocalBuf(2048),sig);
			flats = FFTSubbandFlatness.kr(chain,[300, 500, 1500]);
			Out.kr(0,flats)
		}).load(Server.default);

	}

	makeSpecificGui {
		/*
		controls.put(\show,
			MultiSliderView(win, Rect(0, 0, 256,50))
			.value_(0.dup(nBus))
			.size_(nBus)
			.drawLines_(true)
			.drawRects_(false)
			.indexThumbSize_(256/nBus)
		);	
*/
		win.setInnerExtent(win.bounds.width,win.bounds.height+(24*2));
	}
	
	detect {|net|
		bus.getn(nBus,{|val|
			val.postln;
			//val=val.max(0).min(1); //limit range
			//if(verbose){format("% :  % ",name,val).postln};
			//{
			//	controls[\show].value_(val);
			//	}.defer;
			//net.sendMsg(oscstr,tag,nBus,val.asFloat.round(0.01));
			});
		}	
	
}