CoyoteMIDetector : MIDetector{
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
		
		name="Coyote";
		nchan=1;
		this.setSynthArg();
		statsize=(args[\sendrate]*args[\stattime]).ceil;
		//for plotting
		args[\xaxis]=ControlSpec(-1*args[\stattime],units:'s');
		args[\yaxis]=[0,1].asSpec;
		doDynamic=false;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf,tol=0.15,amp=0|
			var sig,chain,onsets,pips;
			sig=InFeedback.ar(in);
			onsets= Coyote.kr(sig,thresh:args[\thresh],fastMul:args[\fastMul],minDur:args[\minDur]);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			SendReply.kr(onsets,synthname); 
			Out.ar(in,pips*amp);
		}).load(Server.default);

		responder=OSCresponder(Server.default.addr,synthname,{|t,r,m|  sendvalue=1} ).add; 

	}

	makeSpecificGui {
		this.addSoundButton();
		if(doPlot){this.addPlotter()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	calcData{
		buf.get({|val|  sendvalue=val });
		if(doPlot){statarr=statarr.shift(-1).wrapPut(-1,sendvalue)};
	
	}

	updateGui{
		if(doPlot){controls[\plot].setValue(statarr,findSpecs:false)};
	}

	detect {|nets|
		this.calcData();
		{this.updateGui()}.defer;
		if(sendvalue > 0){ 
			nets.do({|net| net.sendMsg(oscstr,tag) });
			sendvalue=0;
			if(doPost){format("% !",name).postln};
		};
	}

	kill{
		responder.remove;
		super.kill();
	}
	
}