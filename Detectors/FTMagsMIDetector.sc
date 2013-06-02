FTMagsMIDetector : MIDetector{
	var doNormalize,startInx,endInx;
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
		this.checkArg(\mult,1.0);
		doNormalize=this.checkArg(\doNormalize,false);
		this.setSynthArg([\mult]);
		startInx=this.checkArg(\startInx,0);
		endInx=this.checkArg(\endInx,128);
		//create default values if not present
		name="FTMags";
		nchan=1;
		//maximum number of values from getn is 1633 so we limit
		args[\fftsize]=if( args[\fftsize] > 2048){2048}{args[\fftsize]};
		datasize=(args[\fftsize]/2).asInteger;
		statsize=(endInx-startInx);
		buf=Buffer.alloc(Server.default,datasize,nchan);
		args[\xaxis]=ControlSpec(1, datasize,\lin).asSpec;
		args[\yaxis]=[0,1,\lin].asSpec;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf,mult=1|
			var sig,chain;
			sig=InFeedback.ar(in);
			chain=FFT(LocalBuf(args[\fftsize]),sig,wintype:args[\fftwintype]);
			chain = PV_MagBuffer(chain*mult, buf);
		}).load(Server.default);
	}

	makeSpecificGui {
		Button(win,30@hrow)
			.states_([["N",Color.white,Color.green],["N",Color.black,Color.red]])	
			.action_({|butt| doNormalize=(butt.value.booleanValue) })
			.value_(doNormalize);
		this.addSlider(\mult,[0.01,100,\exp,0.01].asSpec);
		if(doPlot){this.addPlotter()};
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	calcData{
		buf.getn(startInx,statsize,{|val| sendvalue=if(doNormalize){val.normalize}{val} });
	}

	updateGui{
		if(doPost){format("% :  % ",name,sendvalue).postln};
		if(doPlot){
			controls[\plot].setValue(sendvalue,findSpecs:false);
			if(doDynamic){controls[\plot].calcSpecs}{controls[\plot].specs=args[\yaxis]};
		};
	}

	detect {|nets|
		this.calcData();
		{this.updateGui()}.defer;
		nets.do({|net| net.sendMsg(oscstr,tag,statsize,sendvalue) });
	}
	
}
