FTCrestMIDetector : MIDetector{
	
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
		name="FTCrest";
		nBus=1;
		bus=Bus.control(Server.default,nBus);
		this.setSynthArg();
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,bus|
			var sig,crest,chain;
			sig=InFeedback.ar(in);
			chain = FFT(LocalBuf(2048), sig);
			crest = FFTCrest.kr(chain);
			Out.kr(bus,crest.log)
		}).load(Server.default);
	}

	makeSpecificGui{
		controls.put(\show,NumberBox(win,60@18));
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	detect {|nets|
		bus.get({|val|
			{
			controls[\show].value_(val.round(0.01));
			if(verbose){format("% :  % ",name,val).postln};
			}.defer;
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,val) });
		});	
		
	}
	
	
}