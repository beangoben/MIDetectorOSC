PitchMIDetector : MIDetector{
	
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
		this.checkArg(\minFreq,20);
		this.checkArg(\maxFreq,19000);
		this.checkArg(\ampThreshold,0.01);
		this.checkArg(\peakThreshold,0.5);
		this.checkArg(\median,1);
		name="Pitch";
		nchan=1;
		datasize=args[\datakr];
		buf=Buffer.alloc(Server.default,datasize,nchan);
		this.setSynthArg();
		stats=(\min:0,\max:0,\mean:0,\stdev:0);
		statsize=(args[\sendrate]*args[\stattime]).ceil;
		//for plotting
		args[\xaxis]=ControlSpec(-1*args[\stattime],units:'s');
		args[\yaxis]=ControlSpec(args[\minFreq],args[\maxFreq],\exp,units:'hz').asSpec;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf|
			var sig,freq,hasFreq;
			sig=InFeedback.ar(in);
			# freq, hasFreq = Pitch.kr(sig,minFreq:args[\minFreq],maxFreq:args[\maxFreq],
				ampThreshold:args[\ampThreshold],peakThreshold:args[\peakThreshold],median:args[\median]);
			RecordBuf.kr(freq*hasFreq,buf);
		}).load(Server.default);
	}

	makeSpecificGui {
		controls.put(\show,NumberBox(win,55@18));
		if(doPlot){this.addPlotter()};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	calcData{
		buf.getn(0,datasize,{|val| 
			val=val.select({ |item| item!=0 });
			sendvalue=if(val.isEmpty){0.0}{val.mean};
		});

		if(doPlot || doStats){statarr=statarr.shift(-1).wrapPut(-1,sendvalue)};
		if(doStats){
			stats[\max]=statarr.maxItem;
			stats[\min]=statarr.minItem;
			stats[\mean]=statarr.mean;
			stats[\stdev]=statarr.stdDev(stats[\mean]); 
		};
	
	}

	updateGui{

		controls[\show].value_(sendvalue.round(1));
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
