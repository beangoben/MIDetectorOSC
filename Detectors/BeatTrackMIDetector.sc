BeatTrackMIDetector : MIDetector{
	
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
		this.checkArg(\lock,0.0);
		name="BeatTrack";
		nBus=4;
		bus=Bus.control(Server.default,nBus);	
		this.setSynthArg([\lock]);
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,lock=0.0,amp=0,bus|
			var sig,buffer,chain,pips,quartertick, eighthtick, sixteenthtick, tempo;
			buffer=LocalBuf(1024);
			sig=InFeedback.ar(in);
			chain = FFT(buffer, sig);
			#quartertick, eighthtick, sixteenthtick, tempo = BeatTrack.kr(sig,args[\lock]);
			pips =  Mix.ar(SinOsc.ar([220,880,3520],0.0,Decay.kr([quartertick, eighthtick, sixteenthtick],[0.1,0.05,0.025])));
			Out.ar(in,pips*amp);
			Out.kr(bus,[quartertick, eighthtick, sixteenthtick, tempo])
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addSoundButton();
		Button(win,60@20).states_([["Free"],["Locked"]])
			.value_(0)
			.action_({|butt|
				synth.set(\lock,butt.value);
				args[\lock]=butt.value;
			});
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	detect {|nets|
		bus.getn(nBus,{|val|
			{
			if(verbose){format("% :  % ",name,val).postln};
			}.defer;
			//send messages
			nets.do({|net| net.sendMsg(oscstr,tag,val[0],val[1],val[2],val[3]) });
		});	
	}
	
}
