MIDetector{
	var win,controls,synth,bus,nBus,gui,name,verbose,value,<on,<>in;
	var detectFunc;
	
	*new{
		^super.new;	
	}
	
	genericGui{
		StaticText(win,40@18).string_(name+" : ");
		controls.put(\onOff,
					 Button(win,20@20)
					 .states_([["->",Color.white,Color.green],["||",Color.black,Color.red]])
					 .value_(value.binaryValue)
					 .action_({|butt|
			value=butt.value.booleanValue;
			on=(butt.value==1);
			if(on)
			   {synth=Synth(name++"Detect",[\in,in,\bus,bus,addAction:\addToTail])}
			{synth.free};
		}));	
		
		controls.put(\verbosity,
					 Button(win,40@20)
					 .states_([["Post",Color.black,Color.green],["Post",Color.black,Color.red]])
					 .value_(value.binaryValue)
					 .action_({|butt|
			verbose=butt.value.booleanValue;
			
		}));		
		
	}
	
	onOff {|val|
		controls[\onOff].valueAction_(val);	
	}
	
	kill {	
		bus.free;	
	}
} 