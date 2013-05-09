AmpMIDetector : MIDetector{
	
	*new{|win,in,args|
		^super.newCopyArgs(win,in).init(args);	
	}	
	
	init {|args|
		this.initValues(args);
		super.init();
		this.loadSynthDef();
		super.makeGenericGui();
		this.makeSpecificGui();
	}

	initValues {|args|
		name="Amp";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,bus,mult=1|
			var sig,ampli;
			sig=InFeedback.ar(in);
			ampli=Amplitude.kr(sig,0.1,0.1);
			Out.kr(bus,ampli*mult)
		}).load(Server.default);
	}

	makeSpecificGui {
		EZSlider(win,200@18,"Mult",[0.01,100,\exp,0.01].asSpec,
			{|ez|synth.set(\mult,ez.value) }
			,1,false,labelWidth:30,numberWidth:25);
		controls.put(\show,NumberBox(win,45@18));

		win.setInnerExtent(win.bounds.width,win.bounds.height+24);
	}
	
	detect {|net,tag|
		bus.get({|val|
			if(verbose){format("% :  % ",name,val).post};
			{
				controls[\show].value_(val.round(0.01))}.defer;
				if(val > 0 ){net.sendMsg(oscstr,tag,val)}
			}
		);	
		
	}
	
	
}
