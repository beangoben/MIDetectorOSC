FTPowerMIDetector : MIDetector{
	
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
		this.checkArg(\mult,1.0);
		name="FTPower";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
		this.setSynthArg([\mult]);

	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,mult=1,bus|
		var sig,power,chain;
		sig=InFeedback.ar(in);
		chain = FFT(LocalBuf(2048,1), sig);
		power = FFTPower.kr(chain);
		Out.kr(bus,power*mult)
		}).load(Server.default);
	}

	makeSpecificGui{
		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);
		controls.put(\show,NumberBox(win,60@18));
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