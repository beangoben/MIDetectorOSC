MIDetector{
	var win,<>in,tag,args;
	var controls,synth,bus,nBus,gui,verbose,value,<on;
	var oscstr,name,synthname;
	var detectFunc;

	*new{
		^super.new;	
	}
	
	init{
		verbose=false;
		on=false;
		controls=();
		oscstr="/"++name.toLower;
	}

	makeGenericGui{
		StaticText(win,140@18).string_(format("%: %->%",oscstr,in,tag));
		controls.put(\onOff,
			Button(win,20@20)
			.states_([["->",Color.white,Color.green],["||",Color.black,Color.red]])
			.value_(value.binaryValue)
			.action_({|butt|
				value=butt.value.booleanValue;
				on=(butt.value==1);
				if(on,
					{synth=Synth(synthname,[\in,in,\bus,bus,addAction:\addToTail]++args)},
					{synth.free}
				);
			})
			);	
		
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

	checkArg {|name,value|
		if(args.includes(name),
			nil,
			{args=args++[name,value]}
			);
	}

	getArgValue {|name|
		var tempindx=args.detectIndex({ arg item, i; item==name });
		^ args[tempindx+1] 
	}

} 