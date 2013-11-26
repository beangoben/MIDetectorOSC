RawSignalMIDetector : MIDetector{
	var doNormalize;
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
		doNormalize=this.checkArg(\doNormalize,false);
		this.checkArg(\mult,1.0);
		this.setSynthArg([\mult]);
		name="RawSignal";
		nchan=1;
		//maximum number of values from getn is 1633 so we limit
		datasize=if(args[\fftsize] > 1024){1024}{args[\fftsize]};
		statsize=1;
		statarr=[];
		buf=Buffer.alloc(Server.default,datasize,nchan);
		args[\xaxis]=ControlSpec(0,datasize);
		args[\yaxis]=[-1,1,\lin].asSpec;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,buf,mult=1|
			var sig;
			sig=InFeedback.ar(in);
			RecordBuf.ar(sig*mult,buf);
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
		buf.getn(0,datasize,{|val| sendvalue=if(doNormalize){val.normalize(-1,1)}{val} });
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
		nets.do({|net| net.sendMsg(oscstr,tag,datasize,sendvalue) });
	}
	
}
