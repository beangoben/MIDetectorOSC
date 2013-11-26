MIDetectorManager {
	var win,sendrate,<>nets,ins,tags,defaultargs;
	var tempo,<>detectors,controls,on;
	var fftrate,krrate,fftsize,datakr,datafft;

	*new{|win=nil,sendrate=60,nets=nil,ins=0,tags=0,args|
		^super.newCopyArgs(win,sendrate,nets,ins,tags,args).init()
	}

	init {
		// Initialize stuff
		on=false;
		controls=();
		detectors=List.new();
		defaultargs=defaultargs? ();
		if(defaultargs.isArray){ defaultargs=().putPairs(defaultargs)};
		nets=nets?[NetAddr("127.0.0.1",32000)];
		nets=nets.asArray;
		ins=ins.asArray;
		tags=tags.asArray;
		// calculate rates
		fftsize=defaultargs[\fftsize]?2048;
		fftrate=Server.default.sampleRate/(fftsize*0.5);
		krrate=Server.default.sampleRate/Server.default.options.blockSize;
		// calculate how many values we will have each send cycle
		datakr=(krrate/sendrate).ceil;
		datafft=(fftrate/sendrate).ceil;
		//add some useful values
		defaultargs[\sendrate]=sendrate;
		defaultargs[\datakr]=datakr;
		defaultargs[\datafft]=datafft;
		defaultargs[\fftsize]=fftsize;
		defaultargs[\fftwintype]=1;
		//create a TempoClock for our Osc-sending loop
		tempo=TempoClock.new(sendrate);
		// Create window if not given
		if(win.isNil,{this.makeWindow()});
        // GUI
		win.background=Color.white;
        this.makeMainGui();
        this.makeNetGui();
        win.view.decorator.nextLine;
        //start loop
		this.run;
	}

	makeWindow {
		win = Window.new("MIDetectorManager",Rect(128, 90, 580, 40)).front;
		win.view.decorator = FlowLayout( win.view.bounds,5@5, 4@4);
		win.onClose_({ this.kill });
	}

	makeNetGui {
		nets.do({|item,i|
		controls.put("net"++i,
			StaticText(win,240@20)
			.string_(format("Net% ip:  %, port:  %  ",i,item.ip,item.port))
		);
		if(i%2==1){  win.setInnerExtent(win.bounds.width,win.bounds.height+20)}; //skip line every off net adress
		});
	}

	//update NetAddr
	setNetAddr {|net|
		nets=net.asArray;
		this.makeNetGui();
	}
	//Add a detector
	addOneDetector{|type,in=0,tag=0,args|
		var classTmp;
		classTmp=(type++"MIDetector").asSymbol.asClass;
		//convert from array of pairs to dictionary
		args=args?();
		if(args.isArray){args=().putPairs(args)};
		//add default args
		args.putPairs( defaultargs.getPairs  );
		if( classTmp.notNil){
		if(classTmp.superclass == MIDetector)
			{ detectors.add(classTmp.new(win,in,tag,args)); win.view.decorator.nextLine; }
			{format("MIDetector: Could not find detector of type % !",type).error }
		}
		{ format("MIDetector: Could not find detector of type % !",type).error };

	}
	//Add a detector for each channel
	addDetector{|type,args|
		ins.do({|item,i| this.addOneDetector(type,item,tags[i],args) });
	}

	makeMainGui {
		controls.put(\onOff,
			Button(win, 100@20)
			.states_([
				["ON", Color.white, Color.green],
				["OFF", Color.black, Color.red]
			])
			.action_({|butt|
				on=butt.value.asBoolean;
				detectors.do({|item| item.onOff(butt.value)	});
			})
			.valueAction_(on)
		);

	}

	run {
		tempo.schedAbs(tempo.beats.ceil,{ arg beat, sec;
			if(on){
				detectors.do({|item| if(item.on ){item.detect(nets)};});
			};
		1});
	}

	stop {
		controls[\onOff].valueAction_(0);
	}

	kill {
		this.stop;
		tempo.stop;
		tempo.clear;
	}

	play {
		controls[\onOff].valueAction_(1);
	}

}