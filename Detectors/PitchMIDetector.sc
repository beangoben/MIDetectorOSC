PitchMIDetector : MIDetector{
	
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
		name="Pitch";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,bus|
		var sig,freq,hasFreq;
		sig=InFeedback.ar(in);
		# freq, hasFreq = Pitch.kr(sig,minFreq:20,maxFreq:19000);
		Out.kr(bus,hasFreq*freq)
		}).load(Server.default);
	}

	makeSpecificGui{
		controls.put(\show,NumberBox(win,60@18));
		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net,tag|
		bus.get({|val|
			if(verbose){format("% :  % ",name,val).post};
			{
				controls[\show].value_(val.round(1))}.defer;
				if(val > 0 ){net.sendMsg(oscstr,tag,val)}
			}
		);	
		
	}
	
	
}