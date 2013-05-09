OnsetMIDetector : MIDetector{
	
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
		this.checkArg(\tol,0.15);
		name="Onset";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,tol=0.15,amp=0,bus|
			var sig,buffer,chain,onsets,pips,counter;
			buffer=LocalBuf(1024);
			sig=InFeedback.ar(in);
			chain = FFT(buffer, sig);
			onsets= Onsets.kr(chain, tol, \rcomplex);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,In.kr(bus)+onsets);
		}).load(Server.default);
	}

	makeSpecificGui {
		var tmpindex;

		Button(win,20@20).states_([["S"],["x"]])
			.value_(0)
			.action_({|butt|
				synth.set(\amp,butt.value)
			});

		EZSlider(win,220@18,"tol",[0,1,\lin].asSpec,
			{|ez|synth.set(\tol,ez.value) },
			this.getArgValue(\tol),false,labelWidth:25,numberWidth:35);

		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net,tag|
		bus.get({|val|
			if(val > 0){
				if(verbose){format("% :  % ",name,val).post};
				net.sendMsg(oscstr,tag,val);
				bus.set(0)};
		});	
		
	}
	
}
