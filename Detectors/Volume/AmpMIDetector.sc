AmpMIDetector : MIDetector{

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
		this.checkArg(\mult,1.0);
		this.checkArg(\attackTime,0.01);
		this.checkArg(\releaseTime,0.01);
		name="Amp";
		nchan=1;
		datasize=args[\datakr];
		buf=Buffer.alloc(Server.default,datasize,nchan);
		this.setSynthArg([\mult]);
		stats=(\min:0,\max:0,\mean:0,\stdev:0);
		statsize=(args[\sendrate]*args[\stattime]).ceil;
		//for plotting
		args[\xaxis]=ControlSpec(-1*args[\stattime],units:'s');
		args[\yaxis]=\amp.asSpec;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf,mult=1|
			var sig,ampli;
			sig=InFeedback.ar(in);
			ampli=Amplitude.kr(sig,args[\attackTime],args[\releaseTime]);
			RecordBuf.kr(ampli*mult,buf);
			//Out.ar(1,WhiteNoise.ar()*ampli)
		}).load(Server.default);
	}

	makeSpecificGui {
		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);
		controls.put(\show,NumberBox(win,45@18));
		if(doPlot){this.addPlotter()};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}

	calcData{
		buf.getn(0,datasize,{|val| sendvalue=val.mean });
		if(doPlot || doStats){statarr=statarr.shift(-1).wrapPut(-1,sendvalue)};
		if(doStats){
			stats[\max]=statarr.maxItem;
			stats[\min]=statarr.minItem;
			stats[\mean]=statarr.mean;
			stats[\stdev]=statarr.stdDev(stats[\mean]);
		};

	}

	updateGui{

		controls[\show].value_(sendvalue);
		if(doPost){format("% :  % ",name,sendvalue).postln};
		if(doPlot){
			controls[\plot].setValue(statarr,findSpecs:false);
			if(doDynamic){controls[\plot].calcSpecs}{controls[\plot].specs=args[\yaxis]};
		};
		if(doStats){ this.updateStatsGui()};
	}

	detect {|nets|
		this.calcData();
		{this.updateGui()}.defer;
		nets.do({|net| net.sendMsg(oscstr,tag,sendvalue) });
	}

}
