BeatTrackMIDetector : MIDetector{
	
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
		this.checkArg(\lock,0.0);
		name="BeatTrack";
		nBus=4;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,lock=0.0,amp=0,bus|
			var sig,buffer,chain,pips,quartertick, eighthtick, sixteenthtick, tempo;
			buffer=LocalBuf(1024);
			sig=InFeedback.ar(in);
			chain = FFT(buffer, sig);
			#quartertick, eighthtick, sixteenthtick, tempo = BeatTrack.kr(sig,lock);
			pips =  Mix.ar(SinOsc.ar([220,880,3520],0.0,Decay.kr([quartertick, eighthtick, sixteenthtick],[0.1,0.05,0.025])));
			Out.ar(in,pips*amp);
			Out.kr(bus,[quartertick, eighthtick, sixteenthtick, tempo])
		}).load(Server.default);
	}

	makeSpecificGui {
		var tmpindex;

		Button(win,20@20).states_([["S"],["x"]])
			.value_(0)
			.action_({|butt|
				synth.set(\amp,butt.value)
			});

		Button(win,60@20).states_([["Free"],["Locked"]])
			.value_(0)
			.action_({|butt|
				synth.set(\lock,butt.value)
			});

		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net|
		bus.getn(nBus,{|val|
			if(verbose){format("%: %",name,val).postln};
			net.sendMsg(oscstr,tag,val[0],val[1],val[2],val[3]);
		});	
		
	}
	
}
