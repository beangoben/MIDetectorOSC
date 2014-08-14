PowerBandsMIDetector : MIDetector{
	var doNormSum;
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
		this.checkArg(\scalemode,2);
		this.checkArg(\square,1);
		this.checkArg(\cutfreqs,((1..12)**2)*110);
		doNormSum=this.checkArg(\doNormSum,false);
		this.checkArg(\mult,1);
		name="PowerBands";
		nchan=args[\cutfreqs].size+1;
		datasize=args[\datakr];
		buf=Buffer.alloc(Server.default,datasize,nchan);
		this.setSynthArg([\mult]);
		stats=(\sum:0);
		statsize=nchan;
		statarr=0.dup(nchan);
		//for plotting
		args[\xaxis]=ControlSpec(0,args[\cutfreqs].size,\lin,units:'bin').asSpec;
		args[\yaxis]=[0,1,\lin].asSpec;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf,mult=1|
			var sig,powers,chain;
			sig=InFeedback.ar(in);
			chain=FFT(LocalBuf(args[\fftsize]),sig,wintype:args[\fftwintype]);
			powers = FFTSubbandPower.kr(chain,args[\cutfreqs],args[\square],args[\scalemode]);
			RecordBuf.kr(powers*mult,buf);
		}).load(Server.default);
	}

	makeSpecificGui {

		controls.put(\normsum,Button(win,30@hrow)
		.states_([["Ns",Color.white,Color.green],["Ns",Color.black,Color.red]])
		.action_({|butt| doNormSum=(butt.value.booleanValue)})
		.value_(doNormSum));

		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);

		if(doPlot){
			this.addPlotter();
			controls[\plot].plotMode=\steps;
		};
		if(doStats){this.addStats()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}

	calcData{
		var index=(0..nchan-1)*datasize;
		//checar esto alrato
		buf.getn(0,datasize*nchan,{|val|
			nchan.do({|i| sendvalue[i]=(val[index[0]+i]) })
		});
		if(doPlot || doStats){statarr=sendvalue};
		if(doNormSum){sendvalue=sendvalue.normalizeSum.max(0)};
		if(doStats){
			stats[\sum]=statarr.sum;
			/*
			stats[\stdev_f]=statarr[0].stdDev(stats[\mean_f]);
			stats[\mean_p]=statarr[1].mean;
			stats[\stdev_p]=statarr[1].stdDev(stats[\mean_p]);
			*/
		};

	}

	updateGui{

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
		nets.do({|net| net.sendMsg(oscstr,tag,nchan,sendvalue) });
	}

}
