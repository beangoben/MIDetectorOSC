FTPeakMIDetector : MIDetector{
	
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
		name="FTPeak";
		nchan=2;
		datasize=args[\datakr];
		buf=Buffer.alloc(Server.default,datasize,nchan);
		this.setSynthArg();
		stats=(\mean_f:0,\stdev_f:0,\mean_p:0,\stdev_p:0);
		statsize=(args[\sendrate]*args[\stattime]).ceil;
		//for plotting
		args[\xaxis]=ControlSpec(-1*args[\stattime],units:'s');
		args[\yaxis]=\freq.asSpec;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf|
			var sig,chain,freqmag;
			sig=InFeedback.ar(in);
			chain = FFT(LocalBuf(args[\fftsize]), sig,wintype:args[\fftwintype]);
			freqmag = FFTPeak.kr(chain);
			RecordBuf.kr(freqmag,buf);
		}).load(Server.default);
	}

	makeSpecificGui {
		controls.put(\showfreq,NumberBox(win,60@18));
		controls.put(\showmag,NumberBox(win,60@18));
		if(doPlot){this.addPlotter()};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	calcData{
		buf.getn(0,datasize*nchan,{|val| 
			sendvalue[0]=val.select({|item,i| i.even}).mean;
			sendvalue[1]=val.select({|item,i| i.odd}).mean;
		});

		if(doPlot || doStats){
			statarr[0]=statarr[0].shift(-1).wrapPut(-1,sendvalue[0]);
			statarr[1]=statarr[1].shift(-1).wrapPut(-1,sendvalue[1]);
		};
		if(doStats){
			stats[\mean_f]=statarr[0].mean;
			stats[\stdev_f]=statarr[0].stdDev(stats[\mean_f]); 
			stats[\mean_p]=statarr[1].mean;
			stats[\stdev_p]=statarr[1].stdDev(stats[\mean_p]); 
		};

	}

	updateGui{

		controls[\showfreq].value_(sendvalue[0].round(1));
		controls[\showmag].value_(sendvalue[1].round(0.01));		
		if(doPost){format("% :  % ",name,sendvalue).postln};
		if(doDynamic){controls[\plot].calcSpecs}{controls[\plot].specs=args[\yaxis]};
		if(doPlot){controls[\plot].setValue(statarr[0],findSpecs:false)};
		if(doStats){ this.updateStatsGui()};

	}

	detect {|nets|
		this.calcData();
		{this.updateGui()}.defer;
		nets.do({|net| net.sendMsg(oscstr,tag,sendvalue[0],sendvalue[1]) });
	}
	
}
