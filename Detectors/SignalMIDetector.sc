SignalMIDetector : MIDetector{

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
		name="Signal";
		nBus=512;
		bus=Buffer.alloc(Server.default, nBus); 
		bus.setn(nBus,0.dup(nBus));
	}

	loadSynthDef {	
		SynthDef(synthname,{|in=0,gate=1,bus|
			var sig,writeBuffer;
			sig=InFeedback.ar(in);
			//writeBuffer = Dbufwr(sig, bus,(0..511));
			RecordBuf.ar(sig,bus);
			//Duty.ar(1/SampleRate.ir, 0, writeBuffer);
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addNormalizeButton();
		if(doPlot){this.addPlotter()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}

	detect {|nets|
		bus.getn(0,nBus,{|val|
			
			//do gui related stuff here
			{
			if(verbose){format("% :  % ",name,val).postln};
			if(doPlot){
				controls[\plot].setValue(val,findSpecs: false);
				if(doNormalize){controls[\plot].specs=[-1,1,\lin].asSpec}{controls[\plot].calcSpecs};
				
			};
			}.defer;
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,nBus,val) });
		 });
	}
}