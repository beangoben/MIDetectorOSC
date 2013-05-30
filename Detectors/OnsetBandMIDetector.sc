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
		this.checkArg(\bw,0.25);
		name="OnsetBand";
		nBus=2;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		var bw;
		bw=this.getArgValue(\bw);
		synthname=name++"MIDetect";
	
		SynthDef(synthname,{|in=0,gate=1,tol=0.15,freq=1200,amp=0,bw=0.5,bus|
			var sig,chain,onsets,pips,filter,x,midirq;
			x= 2**(bw / 24.0); 
    		midirq=(x*x - 1) / x; 
			sig=InFeedback.ar(in);
			filter=BBandPass.ar(sig,freq,midirq);
			chain = FFT(LocalBuf(2048), sig);
			onsets= Onsets.kr(chain, tol, \rcomplex);
			pips = SinOsc.ar(freq,0,EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,[In.kr(bus)+onsets,freq]);
		}).load(Server.default);
	}

	makeSpecificGui {

		this.addSoundButton();
		this.addSlider(\tol,[0,1,\lin].asSpec);
		this.addSlider(\freq,\freq.asSpec);
		this.addSlider(\bw,[0.05,10,\exp].asSpec);
		win.setInnerExtent(win.bounds.width,win.bounds.height+(22*2));
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
