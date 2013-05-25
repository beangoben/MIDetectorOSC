OnsetBandMIDetector : MIDetector{
	
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
		this.checkArg(\freq,1200);
		this.checkArg(\tol,0.15);
		this.checkArg(\bw,0.001);
		name="OnsetBand";
		nBus=2;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		var bw;
		bw=this.getArgValue(\bw);
		synthname=name++"MIDetect";
	
		SynthDef(synthname,{|in=0,gate=1,tol=0.15,freq=1200,amp=0,bw=0.001,bus|
			var sig,chain,onsets,pips,counter,lowBin,highBin;
			sig=InFeedback.ar(in);
			sig=BBandPass.ar(sig,freq,bw,150.0/freq.log10);
			chain = FFT(LocalBuf(2048), sig);
			onsets= Onsets.kr(chain, tol, \rcomplex);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,[In.kr(bus)+onsets,freq]);
		}).load(Server.default);
	}

	makeSpecificGui {
		var tmpindex;

		Button(win,20@20).states_([["S"],["x"]])
			.value_(0)
			.action_({|butt|
				synth.set(\amp,butt.value)
			});
		EZSlider(win,220@18,"tol",[0,1,\lin,].asSpec,
			{|ez|synth.set(\tol,ez.value) },
			this.getArgValue(\tol),false,labelWidth:25,numberWidth:35);

		EZSlider(win,225@18,"freq",\freq.asSpec,
			{|ez|synth.set(\freq,ez.value) },
			this.getArgValue(\freq),false,labelWidth:35,numberWidth:45);

		EZSlider(win,225@18,"bw",[0.001,1,\exp].asSpec,
			{|ez|synth.set(\freq,ez.value) },
			this.getArgValue(\bw),false,labelWidth:35,numberWidth:45);

		win.setInnerExtent(win.bounds.width,win.bounds.height+(24*2));
	}
	
	detect {|net|
		bus.getn(nBus,{|val|
			if(val[0] > 0){
				if(verbose){format("%! : %",name,val[1]).postln};
				net.sendMsg(oscstr,tag,val[1]);
				bus.setn([0,val[1]])};
		});	
		
	}
	
}
