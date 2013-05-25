FTPowerMIDetector : MIDetector{
	
	*new{|win,in=0,tag=0,args=nil|
		^super.newCopyArgs(win,in,tag,args).init();	
	}	
	
	init {
		this.initValues();
		super.init();
		this.loadSynthDef();
		super.makeGenericGui();
		this.makeSpecificGui();
	}

	initValues {
		//create default values if not present
		if(args.isNil,{args=[]});
		this.checkArg(\mult,1.0);
		name="FTPower";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,mult=1,bus|
		var sig,power,chain;
		sig=InFeedback.ar(in);
		chain = FFT(LocalBuf(2048,1), sig);
		power = FFTPower.kr(chain);
		Out.kr(bus,power*mult)
		}).load(Server.default);
	}

	makeSpecificGui{
		EZSlider(win,200@18,"Mult",[0.01,100,\exp,0.01].asSpec,
			{|ez|synth.set(\mult,ez.value) }
			,this.getArgValue(\mult),false,labelWidth:30,numberWidth:25);

		controls.put(\show,NumberBox(win,60@18));
		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net|
		bus.get({|val|
			if(verbose){format("% : %  ",name,val).postln};
			{
			controls[\show].value_(val.round(0.01));
			}.defer;
			net.sendMsg(oscstr,tag,val)
			}
		);	
		
	}
	
	
}