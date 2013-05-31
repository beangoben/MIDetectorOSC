CoyoteMIDetector : MIDetector{
	
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
		this.checkArg(\thres,0.015);
		this.checkArg(\sense,0.5);
		name="Coyote";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		this.setSynthArg([\tol]);
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,amp=0,bus|
			var sig,chain,onsets,pips;
			sig=InFeedback.ar(in);
			onsets= Coyote.kr(sig,thres:args[\thres],fastmul:args[\sense]);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,In.kr(bus)+onsets);
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addSoundButton();
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	detect {|nets|
		bus.get({|val|
			if(val > 0){
			if(verbose){format("% :  % ",name,val).postln};
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,val) });
			bus.set(0);
			}
		});	
	}
	
}
