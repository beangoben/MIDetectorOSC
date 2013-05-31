PitchMIDetector : MIDetector{
	
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
		this.setSynthArg();
		name="Pitch";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,bus|
			var sig,freq,hasFreq;
			sig=InFeedback.ar(in);
			# freq, hasFreq = Pitch.kr(sig,minFreq:20,maxFreq:19000);
			Out.kr(bus,hasFreq*freq)
		}).load(Server.default);
	}

	makeSpecificGui{
		controls.put(\show,NumberBox(win,60@18));
		if(doPlot){this.addPlotter(yaxis:\freq.asSpec)};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	detect {|nets|
		bus.get({|val|
			{
			controls[\show].value_(val.round(1));
			if(verbose){format("% :  % ",name,val).postln};
			}.defer;
			//send messages
			if(val > 0 ){ nets.do({|net| net.sendMsg(oscstr,tag,val) }) };
		});	
	}
	
	
}