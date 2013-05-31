MIDetector{
	var win,<>in,tag,args;
	var synth,synthargs,synthname,bus,nBus,stats;
	var dataBuf,doNormalize,doStats,doPlot;
	var controls,hextend,slidercount;
	var oscstr,name,verbose,value,<on;
	var detectFunc;

	*new{
		^super.new;	
	}
	
	init{
		verbose=false;
		on=false;
		controls=();
		stats=();
		oscstr="/"++name.toLower;
		doNormalize=false;
		doStats=false;
		doPlot=true;
		hextend=0;
		slidercount=0;
		synthname=format("%MIDetect_%",name,1000.rand); //put rand to avoid duplicate names
	}

	makeGenericGui{
		StaticText(win,140@20).string_(format("%: %->%",oscstr,in,tag));
		
		controls.put(\onOff,
			Button(win,40@20)
			.states_([["->",Color.white,Color.green],["||",Color.black,Color.red]])
			.value_(on)
			.action_({|butt|
				value=butt.value.booleanValue;
				on=(butt.value==1);
				if(value,
					{synth=Synth(synthname,[\in,in,\bus,bus,addAction:\addToTail]++synthargs)},
					{synth.free}
				);
			})
			);	
		
		controls.put(\verbosity,
			Button(win,40@20)
			.states_([["Post",Color.black,Color.green],["Post",Color.black,Color.red]])
			.value_(false)
			.action_({|butt|
			verbose=butt.value.booleanValue;
		}));	
		hextend=hextend+20;	
	}

	addStats { 
		controls.put(\max,EZNumber(win,100@20,"max"));
		controls.put(\min,EZNumber(win,100@20,"min"));
		controls.put(\mean,EZNumber(win,100@20,"mean"));
		controls.put(\var,EZNumber(win,100@20,"var"));

		hextend=hextend+20;
	}

	addSlider { |name,spec|

		EZSlider(win,250@18,name,spec,
			{|ez|synth.set(name,ez.value) },
			args[name],false,labelWidth:35,numberWidth:45);
		slidercount=slidercount+1;
		if(slidercount%2 == 0){hextend=hextend+20;}
	}

	addSoundButton {
		Button(win,20@20).states_([["S"],["x"]])
			.value_(0)
			.action_({|butt|
				synth.set(\amp,butt.value)
			});
	}

	addNormalizeButton {
		Button(win,20@20)
			.states_([["_",Color.white,Color.black],["N",Color.black,Color.white]])
			.value_(0)
			.action_({|butt|
				doNormalize=(butt.value.booleanValue)
			});
	}


	addPlotter{|xaxis,yaxis|
		controls.put(\plot,
			Plotter(name++"plot", Rect(0, 0, 512,100),win)
			.plotMode_(\linear)
			.value_(0.dup(nBus))
			.editMode_(false)
			.setProperties(
				\backgroundColor, Color.white
			)
		);	

		if(yaxis.notNil){controls[\plot].specs_(yaxis)};
		if(xaxis.notNil){controls[\plot].domainSpecs_(xaxis)};
		hextend=hextend+100;
	}

	showMultiSlider{
			controls.put(\show,
			MultiSliderView(win, Rect(0, 0, 256,50))
			.value_(0.dup(nBus))
			.size_(nBus)
			.drawLines_(true)
			.drawRects_(false)
			.indexThumbSize_(256/nBus)
		);	
	}

	onOff {|val|
		controls[\onOff].valueAction_(val);	
	}
	
	kill {	
		controls[\onOff].valueAction_(0);	
		bus.free;
	}

	checkArg {|name,value|
		if(args[name].isNil,{args.put(name,value)});
		^ args[name];
	}

	setSynthArg {|names|
		if(synthargs.isNil,{synthargs=[]});
		if(names.notNil){
			names.do({|item|
				synthargs=synthargs++[item,args[item]]
			});
		};
	}


	updateStats{|val|
		controls[\max].value_(val.maxItem);
		controls[\min].value_(val.minItem);
		controls[\mean].value_(val.mean);
		controls[\var].value_(val.variance);
	}
} 