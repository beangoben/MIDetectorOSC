FTPeakMIDetector : MIDetector{
	
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
		name="FTPeak";
		nBus=2;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,bus|
		var sig,freqmag,chain;
		sig=InFeedback.ar(in);
		chain = FFT(LocalBuf(2048,1), sig);
		freqmag = FFTPeak.kr(chain);
		Out.kr(bus,freqmag)
		}).load(Server.default);
	}

	makeSpecificGui{
		controls.put(\showfreq,NumberBox(win,60@18));
		controls.put(\showmag,NumberBox(win,60@18));
		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net|
		bus.getn(2,{|val|
			if(verbose){format("% : % , % ",name,val[0],val[1]).postln};
			{
				controls[\showfreq].value_(val[0].round(1));
				controls[\showmag].value_(val[1].round(0.01));
			}.defer;
			net.sendMsg(oscstr,tag,val[0],val[1])
			}
		);	
		
	}
	
	
}