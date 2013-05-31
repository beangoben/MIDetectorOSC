MIDetectorManager {
	var win,<>net,in,tag;
	var tempo,detectors,controls,on;
	
	*new{|win=nil,types=nil,rate=60,net=nil,in=0,tag=0|
		^super.newCopyArgs(win,net,in,tag).init(types,rate)
	}	
	
	init {|types,rate|
		// Initialize stuff
		on=false;
		detectors=List.new();
		controls=();
		if(net.isNil,{net=[NetAddr("127.0.0.1",12000)]});
		if((net.isArray).not,{net=[net]} );
		if((in.isArray).not,{in=[in]} );
		if((tag.isArray).not,{tag=[tag]});
		//create a TempoClock for our Osc-sending loop
		tempo=TempoClock.new(rate);	
		// Create window if not given
		if(win.isNil,{this.makeWindow()});
        // GUI
        this.makeMainGui();
        this.makeNetGui();
		// Create Detectors if an array with types is given
		if(types.isNil,
			nil,
			{
				types.do({|item|
					this.addDetector(item,nil)
				})
			}
		);
		this.run;
	}
	
	makeWindow {
		win = Window.new("MIDetectorManager",Rect(128, 90, 550, 64)).front;
		win.view.decorator = FlowLayout( win.view.bounds,10@10, 4@4);
		win.onClose_({ this.kill });
	}

	makeNetGui {
		controls.put(\netbox,
			StaticText(win,240@20)
			.string_(format("IP:  %   Port:  %  ",net[0].ip,net[0].port))
		);
		win.view.decorator.nextLine;
	}
	//update NetAddr
	setNetAddr {|net|
		this.net=[net];
		controls[\netbox].string=format("IP:  %   Port:  %  ",net[0].ip,net[0].port);
	}

	addDetector{|type,args|
		var classTmp;
		classTmp=(type++"MIDetector").asSymbol.asClass;
		if( (classTmp.notNil) && (classTmp.superclass == MIDetector) ,
			{
			in.do({
				arg item,i;
				detectors.add(classTmp.new(win,item,tag[i],args));
				win.view.decorator.nextLine;
			});
			},
			{format("MIDetector: Could not find detector % !",type).error; }
		);	
	}

	makeMainGui {
		controls.put(\onOff,
			Button(win, 100@20)
			.states_([
				["ON", Color.black, Color.red],
				["OFF", Color.white, Color.black],
			])
			.action_({ arg butt;
				on=(butt.value==1);
				detectors.do({|item|
					item.onOff(butt.value);	
				});	
			})
			.valueAction_(on.binaryValue)
		);

	}

	run {	
		tempo.schedAbs(tempo.beats.ceil,{ arg beat, sec;
			if(on){	
				detectors.do({|item| if(item.on ){item.detect(net)};});
			};
		1});	
	}	

	kill {
		detectors.do({|item|
			item.kill();	
		});
		tempo.clear;
		tempo.stop;		
		controls[\onOff].valueAction_(0);

	}
	
	
	
}