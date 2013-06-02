CoyoteBandMIDetector : MIDetector{
	var responder;

	*new{|win,in=0,tag=0,args=nil|
		^super.newCopyArgs(win,in,tag,args).init();	
	}	
	
	init {
		super.init1();
		this.initValues();
		super.init2();
		this.loadSynthDef();
		super.makeGenericGui();
		this.makeSpecificGui();
	}

	initValues {
		//create default values if not present
		this.checkArg(\thresh,0.015);
		this.checkArg(\fastMul,0.1);
		this.checkArg(\minDur,0.1);
		this.checkArg(\freq,1200);
		this.checkArg(\bw,0.25);
		this.checkArg(\mult,1.0);
		name="CoyoteBand";
		nchan=1;
		this.setSynthArg([\freq,\bw,\mult]);
		statsize=(args[\sendrate]*args[\stattime]).ceil;
		//for plotting
		args[\xaxis]=ControlSpec(-1*args[\stattime],units:'s');
		args[\yaxis]=[0,1].asSpec;
		doDynamic=false;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf,tol=0.15,amp=0,freq=440,bw=0.25,mult=1|
			var sig,chain,onsets,pips,filter,midirq,x;
			x= 2**(bw / 24.0); 
    		midirq=(x*x - 1) / x; 
			sig=InFeedback.ar(in);
			filter=BBandPass.ar(sig,freq,midirq,mult);
			onsets= Coyote.kr(filter,thresh:args[\thresh],fastMul:args[\fastMul],minDur:args[\minDur]);
			pips = SinOsc.ar(freq,0,EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			SendReply.kr(onsets,synthname); 
			Out.ar(in,pips*amp);
		}).load(Server.default);

		responder=OSCresponder(Server.default.addr,synthname,{|t,r,m|  sendvalue=1} ).add; 

	}

	makeSpecificGui {
		this.addSoundButton();
		this.addSlider(\mult,[0.001,10,\exp].asSpec);
		this.addSlider(\freq,\freq.asSpec);
		//controls[\freq].action_({|ez| synth.set(name,ez.value); args[\freq]=ez.value });
		this.addSlider(\bw,[0.001,2,\exp].asSpec);
		if(doPlot){this.addPlotter()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	calcData{
		if(doPlot){statarr=statarr.shift(-1).wrapPut(-1,sendvalue)};
	
	}

	updateGui{
		if(doPlot){controls[\plot].setValue(statarr,findSpecs:false)};
	}

	detect {|nets|
		this.calcData();
		{this.updateGui()}.defer;
		if(sendvalue > 0){ 
			nets.do({|net| net.sendMsg(oscstr,tag,args[\freq]) });
			sendvalue=0;
			if(doPost){format("% %!",name,args[\freq]).postln};
		};
	}

	kill{
		responder.remove;
		super.kill();
	}	
}