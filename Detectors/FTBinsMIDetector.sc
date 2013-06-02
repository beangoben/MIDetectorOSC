FTBinsMIDetector : MIDetector{

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
		name="FTBins";
		nchan=1;
		//maximum number of values from getn is 1633 so we limit
		datasize= if( args[\fftsize] > 1024) {1024};
		args[\fftsize]=datasize;
		buf=Buffer.alloc(Server.default,datasize,nchan);
		doPlot=false;
	}

	loadSynthDef {
		SynthDef(synthname,{|in=0,gate=1,buf|
			var sig,chain;
			sig=InFeedback.ar(in);
			chain=FFT(LocalBuf(args[\fftsize]),sig,wintype:args[\fftwintype]);
			chain = PV_Copy(chain, buf);
		}).load(Server.default);
	}

	makeSpecificGui {
		StaticText(win,120@hrow).string_(format("FFTsize: %",datasize));
		win.setInnerExtent(win.bounds.width,win.bounds.height+hextend);
	}
	
	calcData{
		buf.getn(0,datasize,{|val| sendvalue=val });
	}

	updateGui{
	
		if(doPost){format("% :  % ",name,sendvalue).postln};
	}

	detect {|nets|
		this.calcData();
		{this.updateGui()}.defer;
		nets.do({|net| net.sendMsg(oscstr,tag,datasize,sendvalue) });
	}
	
}