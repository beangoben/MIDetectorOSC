OnsetMIDetector : MIDetector{
	
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
		this.checkArg(\tol,0.15);
		this.checkArg(\odftype,\rcomplex);
		name="Onset";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		this.setSynthArg([\tol]);
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,tol=0.15,amp=0,bus|
			var sig,chain,onsets,pips;
			sig=InFeedback.ar(in);
			chain = FFT(LocalBuf(1024), sig);
			onsets= Onsets.kr(chain, tol, args[\odftype]);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,In.kr(bus)+onsets);
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addSoundButton();
		this.addBasicSlider(\tol,[0,1,\lin].asSpec);
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
