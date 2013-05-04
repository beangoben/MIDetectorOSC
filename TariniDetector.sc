TariniDetector : Detector{
	
	*new{|win,in|
		^super.newCopyArgs(win,in).init();	
	}	
	
	init {
		name=\tarini;
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
		verbose=false;
		on=false;
		controls=();
		
SynthDef(\tariniDetect,{|in=0,gate=1,bus,freqi=30|
var sig,freq,hasFreq;
sig=InFeedback.ar(in);
# freq, hasFreq = Tarini.kr(sig);
Out.kr(bus,hasFreq*freq)
}).load(Server.default);

		
		super.genericGui();
		this.specificGui();
		
	}
	
	specificGui{
		controls.put(\show,NumberBox(win,60@18));
	}
	
	detect {|net,tag|
		bus.get({|val|
			if(verbose){format("% :  % ",name,val).post};
			{controls[\show].value_(val.round(1))}.defer;
			if(val > 0 ){net.sendMsg("/"++name,tag,val)};
		});	
		
	}
	
	
}