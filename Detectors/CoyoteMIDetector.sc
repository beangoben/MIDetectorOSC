CoyoteMIDetector : MIDetector{
	
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
		this.checkArg(\thres,0.015);
		this.checkArg(\sense,0.5);
		name="Coyote";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		var thres,sense;
		thres=this.getArgValue(\thres);
		sense=this.getArgValue(\sense);
		synthname=name++"MIDetect";

		SynthDef(synthname,{|in=0,gate=1,amp=0,bus|
			var sig,buffer,chain,onsets,pips,counter;
			buffer=LocalBuf(1024);
			sig=InFeedback.ar(in);
			onsets= Coyote.kr(sig,thres:thres,fastmul:sense);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,In.kr(bus)+onsets);
		}).load(Server.default);
	}

	makeSpecificGui {

		this.addSoundButton();
		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net|
		bus.get({|val|
			if(val > 0){
				if(verbose){format("%!",name).postln};
				net.sendMsg(oscstr,tag);
				bus.set(0)
			};
		});	
		
	}
	
}
