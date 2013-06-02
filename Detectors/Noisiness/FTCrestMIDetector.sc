FTCrestMIDetector : MIDetector{
	var doLog;

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
		this.checkArg(\freqlo,0);
		this.checkArg(\freqhi,50000);
		doLog=this.checkArg(\doLog,true);
		name="FTCrest";
		nchan=1;
		datasize=args[\datakr];
		buf=Buffer.alloc(Server.default,datasize,nchan);
		stats=(\min:0,\max:0,\mean:0,\stdev:0);
		statsize=(args[\sendrate]*args[\stattime]).ceil;
		//for plotting
		args[\xaxis]=ControlSpec(-1*args[\stattime],units:'s');
		args[\yaxis]=[1,1000,\exp].asSpec;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf,mult=1|
			var sig,crest,chain;
			sig=InFeedback.ar(in);
			chain=FFT(LocalBuf(args[\fftsize]),sig,wintype:args[\fftwintype]);
			crest = FFTCrest.kr(chain, freqlo: args[\freqlo], freqhi: args[\freqhi]);
			RecordBuf.kr(crest,buf);
		}).load(Server.default);
	}

	makeSpecificGui {
		
		controls.put(\log,Button(win,30@hrow)
			.states_([["Log",Color.white,Color.green],["Log",Color.black,Color.red]])	
			.action_({|butt|
				doLog=(butt.value.booleanValue);
				args[\yaxis]=if(doLog){[0,10,\lin].asSpec}{[1,1000,\exp].asSpec};
				if(doPlot){ controls[\plot].specs=args[\yaxis]};
				})
			.value_(doLog));

		controls.put(\show,NumberBox(win,45@18));
		if(doPlot){this.addPlotter()};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	calcData{
		buf.getn(0,datasize,{|val| sendvalue=if(doLog){(val.mean).log}{val.mean} });
		if(doPlot || doStats){statarr=statarr.shift(-1).wrapPut(-1,sendvalue)};
		if(doStats){
			stats[\max]=statarr.maxItem;
			stats[\min]=statarr.minItem;
			stats[\mean]=statarr.mean;
			stats[\stdev]=statarr.stdDev(stats[\mean]); 
		};
	
	}

	updateGui{

		controls[\show].value_(sendvalue.round(0.1));
		if(doPost){format("% :  % ",name,sendvalue).postln};
		if(doDynamic){controls[\plot].calcSpecs}{controls[\plot].specs=args[\yaxis]};
		if(doPlot){controls[\plot].setValue(statarr,findSpecs:false)};
		if(doStats){ this.updateStatsGui()};
	}

	detect {|nets|
		this.calcData();
		{this.updateGui()}.defer;
		nets.do({|net| net.sendMsg(oscstr,tag,sendvalue) });
	}
	
}
