OnsetMIDetector : MIDetector{
	
	*new{|win,in|
		^super.newCopyArgs(win,in).init();	
	}	
	
	init {
		name=\onset;
		nBus=1;
		bus=Bus.control(Server.default,nBus);	
		value=0;
		verbose=false;
		on=false;
		controls=();
		

SynthDef(\OnsetMIDetect,{|in=0,gate=1,tol=0.3,amp=0,bus|
var sig,buffer,chain,onsets,pips,counter;
buffer=LocalBuf(1024);
sig=InFeedback.ar(in);
chain = FFT(buffer, sig);
onsets= Onsets.kr(chain, tol, \complex);
pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
Out.ar(in,pips*amp);
Out.kr(bus,In.kr(bus)+onsets);
}).load(Server.default);
		
		super.genericGui();
		this.specificGui();
		
	}
	
	specificGui {
	Button(win,20@20).states_([[" "],["x"]])
	.value_(0)
	.action_({|butt|
	synth.set(\amp,butt.value)
	});
	EZSlider(win,220@18,"tol",[0,1,\lin].asSpec,
	{|ez|synth.set(\tol,ez.value) },0.15,false,labelWidth:25,numberWidth:35);
	}
	
	detect {|net,tag|
		bus.get({|val|
			if(val > 0){
			if(verbose){format("% :  % ",name,val).post};
			net.sendMsg("/"++name,tag,val);
			bus.set(0)};
		});	
		
	}
	
	
}