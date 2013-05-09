OnsetMIDetector : MIDetector{
	
	*new{|win,in,args|
		^super.newCopyArgs(win,in).init(args);	
	}	
	
	init {|args|
		this.initValues(args);
		super.init();
		this.loadSynthDef();
		super.makeGenericGui();
		this.makeSpecificGui();
	}

	initValues {|args|
		name="Onset";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,tol=0.3,amp=0,bus|
			var sig,buffer,chain,onsets,pips,counter;
			buffer=LocalBuf(1024);
			sig=InFeedback.ar(in);
			chain = FFT(buffer, sig);
			onsets= Onsets.kr(chain, tol, \complex);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,In.kr(bus)+onsets);
		}).load(Server.default);
	}

	makeSpecificGui {
		Button(win,20@20).states_([[" "],["x"]])
			.value_(0)
			.action_({|butt|
				synth.set(\amp,butt.value)
			});

		EZSlider(win,220@18,"tol",[0,1,\lin].asSpec,
			{|ez|synth.set(\tol,ez.value) },
			0.15,false,labelWidth:25,numberWidth:35);

		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net,tag|
		bus.get({|val|
			if(val > 0){
				if(verbose){format("% :  % ",name,val).post};
				net.sendMsg("/"++name,tag,val);
				bus.set(0)};
		});	
		
	}
	
}
