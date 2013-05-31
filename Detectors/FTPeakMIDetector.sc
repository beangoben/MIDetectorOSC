FTPeakMIDetector : MIDetector{
	
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
		//create default values if not present
		if(args.isNil,{args=[]});
		name="FTPeak";
		nBus=2;
		bus=Bus.control(Server.default,nBus);	
		this.setSynthArg();
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,bus|
			var sig,freqmag,chain;
			sig=InFeedback.ar(in);
			chain = FFT(LocalBuf(2048), sig);
			freqmag = FFTPeak.kr(chain);
			Out.kr(bus,freqmag)
		}).load(Server.default);
	}

	makeSpecificGui{
		controls.put(\showfreq,NumberBox(win,60@18));
		controls.put(\showmag,NumberBox(win,60@18));
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	detect {|nets|
		bus.getn(nBus,{|val|
			{
			if(verbose){format("% :  % ",name,val).postln};
			controls[\showfreq].value_(val.round(1));
			controls[\showmag].value_(val.round(0.01));
			}.defer;
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,val[0],val[1]) });
		});
		
	}
	
	
}