WAmpMIDetector : MIDetector{
	
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
		this.checkArg(\mult,1.0);
		this.checkArg(\winSize,1.0);
		name="WAmp";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
		this.setSynthArg([\mult]);
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,bus,mult=1|
			var sig,ampli;
			sig=InFeedback.ar(in);
			ampli=WAmp.kr(sig,winSize:args[\winSize]);
			Out.kr(bus,ampli*mult)
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);
		controls.put(\show,NumberBox(win,45@18));
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	detect {|nets|
		bus.get({|val|
			{
			controls[\show].value_(val.round(1));
			if(verbose){format("% :  % ",name,val).postln};
			}.defer;
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,val) });
		});	
	}
	
	
}
