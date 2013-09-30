MIDetector{
	var win,<>in,tag,args;
	var synth,synthargs,synthname,buf,nchan,datasize;
	var statarr,stats,statsize;
	var doDynamic,doStats,doPlot,doPost;
	var controls,hextend,slidercount,hrow,hrowgap;
	var oscstr,name,sendvalue,<on;

	*new{
		^super.new;
	}

	//part1
	init1{
		doDynamic=false;
		doStats=this.checkArg(\doStats,false);
		doPlot=this.checkArg(\doPlot,true);
		doPost=this.checkArg(\doPost,true);
		on=false;
		this.checkArg(\stattime,4);
		controls=();
		hextend=0; //size to extend the window
		hrow=18;//size of a row
		hrowgap=hrow+(win.view.decorator.gap.y);//size of a row
		slidercount=0; //count how many sliders to extend the window
	}
	//part2, values to initialize after specific values have been initialized
	init2{
		synthargs=synthargs?[];
		oscstr="/"++name.toLower;
		synthname=format("%MIDetect_%",name,1000.rand); //put rand to avoid duplicate names
		if(nchan == 1){sendvalue=0}{sendvalue=0.dup(nchan)};
		if(statarr.isNil && (doPlot || doStats)){
			if(nchan == 1){statarr=0.dup(statsize)}{statarr=0.dup(statsize) dup: nchan};
		};
		stats=stats ? () ;
	}

	makeGenericGui{
		StaticText(win,140@hrow).string_(format("%: %->%",oscstr,in,tag));

		controls.put(\onOff,
			Button(win,40@hrow)
			.states_([["->",Color.white,Color.green],["||",Color.black,Color.red]])
			.value_(on)
			.action_({|butt|
				on=butt.value.booleanValue;
				if(on,
					{
						[synthname,[\in,in,\buf,buf]++synthargs].postln;
					synth=Synth(synthname,[\in,in,\buf,buf]++synthargs)
					//synth=Synth(synthname)
					},
					{synth?synth.free}
				);
			})
			);
		if(doPost){this.addPostButton};
		if(doPlot){this.addDynamicButton()};

		hextend=hextend+hrowgap;
	}

	addPostButton{
		controls.put(\doPost,
		Button(win,20@hrow)
			.states_([["P",Color.white,Color.green],["P",Color.black,Color.red]])
			.action_({|butt| doPost=butt.value.booleanValue})
			.valueAction_(false)
		);
	}

	addStats {
		stats.keysDo({|key|
			StaticText(win,60@hrow).string_(key).align_(\right);
			controls.put(key,NumberBox(win,60@hrow))
		 });
		hextend=hextend+hrowgap*((stats.size/4).ceil);
	}

	addSlider { |name,spec|

		EZSlider(win,240@18,name,spec,
			{|ez|synth.set(name,ez.value) },
			args[name],false,labelWidth:35,numberWidth:45);
		slidercount=slidercount+1;
		if(slidercount%2 == 0){hextend=hextend+hrowgap;}
	}

	addSoundButton {
		Button(win,20@hrow).states_([["S"],["x"]])
			.value_(0)
			.action_({|butt|
				synth.set(\amp,butt.value)
			});
	}

	addDynamicButton {
		if(doPlot){
		Button(win,30@hrow)
			.states_([["Dyn",Color.white,Color.green],["Dyn",Color.black,Color.red]])
			.action_({|butt|
				doDynamic=(butt.value.booleanValue)
			})
			.value_(doDynamic);
		};
	}


	addPlotter{
		controls.put(\plot,
			Plotter(name++"plot", Rect(0, 0, 512,5*hrow),win)
			.plotMode_(\linear)
			.editMode_(false)
			.value_(0.dup(statsize))
			.setProperties(
				\backgroundColor, Color.white,
				\gridLineSmoothing, true
			)
		);

		if(args[\yaxis].notNil){controls[\plot].specs_(args[\yaxis])};
		if(args[\xaxis].notNil){controls[\plot].domainSpecs_(args[\xaxis])};
		hextend=hextend+(hrow*4)+hrowgap;
	}

	showMultiSlider{
			controls.put(\show,
			MultiSliderView(win, Rect(0, 0, 256,hrow*3))
			.value_(0.dup(nchan))
			.size_(nchan)
			.drawLines_(true)
			.drawRects_(false)
			.indexThumbSize_(256/nchan)
		);
		hextend=hextend+(hrow*2)+hrowgap;
	}

	onOff {|val|
		controls[\onOff].valueAction=val;
	}

	kill {
		controls[\onOff].valueAction=0;
		buf?buf.free;
		statarr?statarr.free;
	}

	checkArg {|name,value|
		if(args[name].isNil,{args.put(name,value)});
		^ args[name];
	}

	setSynthArg {|names|
		synthargs=synthargs?[];
		if(names.notNil){
			names.do({|item|
				synthargs=synthargs++[item,args[item]]
			});
		};
	}

	updateStatsGui{
		stats.keysValuesDo({|key,value| controls[key].value_(value)});
	}
} 