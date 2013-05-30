CoyoteBandMIDetector : MIDetector{
	
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
		this.checkArg(\freq,1200);
		this.checkArg(\bw,0.25);
		name="CoyoteBand";
		nBus=2;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		var thres,sense,freq;
		thres=this.getArgValue(\thres);
		sense=this.getArgValue(\sense);
		sense=this.getArgValue(\bw);
		freq=this.getArgValue(\freq);
		synthname=name++"MIDetect";

		SynthDef(synthname,{|in=0,gate=1,bw=0.25,freq=1200,amp=0,bus|
			var sig,buffer,chain,onsets,pips,filter,midirq,x;
			x= 2**(bw / 24.0); 
    		midirq=(x*x - 1) / x; 
			buffer=LocalBuf(1024);
			sig=InFeedback.ar(in);
			filter=BBandPass.ar(sig,freq,midirq);
			onsets= Coyote.kr(sig,thres:thres,fastmul:sense);
			pips = SinOsc.ar(freq,0,EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(in,pips*amp);
			Out.kr(bus,[In.kr(bus)+onsets,freq]);
		}).load(Server.default);
	}

	makeSpecificGui {

		this.addSoundButton();
		this.addSlider(\freq,\freq.asSpec);
		this.addSlider(\bw,[0.05,10,\exp].asSpec);
		win.setInnerExtent(win.bounds.width,win.bounds.height+(22*2));
	}
	
	detect {|net|
		bus.getn(nBus,{|val|
			if(val[0] > 0){
				if(verbose){format("%:%!",name,val[1]).postln};
				net.sendMsg(oscstr,tag,val[1]);
				bus.set([0,val[1]])
			};
		});	
		
	}
	
}
