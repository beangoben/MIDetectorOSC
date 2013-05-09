AmpMIDetector : MIDetector{
	
	*new{|win,in|
		^super.newCopyArgs(win,in).init();	
	}	
	
	init {
		name="Amp";
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
		verbose=false;
		on=false;
		controls=();
		
		SynthDef(\AmpMIDetect,{|in=0,gate=1,bus,mult=1|
			var sig,ampli;
			sig=InFeedback.ar(in);
			ampli=Amplitude.kr(sig,0.1,0.1);
			Out.kr(bus,ampli*mult)
		}).load(Server.default);
		
		super.genericGui();
		this.specificGui();
		
	}
	
	specificGui {
		EZSlider(win,200@18,"Mult",[0.01,100,\exp,0.01].asSpec,
					   {|ez|synth.set(\mult,ez.value) },1,false,labelWidth:30,numberWidth:25);
		controls.put(\show,NumberBox(win,45@18));
		
	}
	
	detect {|net,tag|
		bus.get({|val|
			if(verbose){format("% :  % ",name,val).post};
			{controls[\show].value_(val.round(0.01))}.defer;
			net.sendMsg("/"++name,tag,val);
		});	
		
	}
	
	
}