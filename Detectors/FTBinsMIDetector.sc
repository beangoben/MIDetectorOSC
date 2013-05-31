FTBinsMIDetector : MIDetector{

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
		this.setSynthArg();
		name="FTBins";
		nBus=1024;
		bus=Buffer.alloc(Server.default, nBus); 
		bus.setn(nBus,0.dup(nBus));
	}

	loadSynthDef {	
		SynthDef(synthname,{|in=0,gate=1,bus|
			var sig,chain;
			sig=InFeedback.ar(in);
			chain=FFT(LocalBuf(1024),sig);
			chain = PV_Copy(chain, bus);
		}).load(Server.default);
	}

	makeSpecificGui {	
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}

	detect {|nets|
		bus.getn(0,nBus,{|val|
			//do gui related stuff here
			{
			if(verbose){format("% :  % ",name,val).postln};
			}.defer;
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,nBus,val) });
		 });
	}
}