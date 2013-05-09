MIDetectorManager {
	var win,<>net,in,tag;
	var tempo,detectors,controls,on;
	
	
	*new{|win=nil,types=nil,rate=60,net=nil,in=0,tag=0|
		^super.newCopyArgs(win,net,in,tag).init(types,rate)
	}	
	
	init {|types,rate|
		// Initialize stuff
		on=false;
		detectors=();
		controls=();
		if(net.isNil,{net=NetAddr("127.0.0.1",12000)});

		//create a TempoClock for our Osc-sending loop
		tempo=TempoClock.new(rate);	
		// Create window if not given
		if(win.isNil,{this.makeWindow()});
        // GUI
        this.makeNetGui();
        this.makeMainGui();
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
		win = Window.new("MIDetectorManager",Rect(128, 90, 400, 64)).front;
		win.view.decorator = FlowLayout( win.view.bounds,10@10, 4@4);
		win.onClose_({ this.kill });
	}

	makeNetGui {

		controls.put(\netbox,
			StaticText(win,180@20)
			.string_(format("IP:  %   Port:  %  ",net.ip,net.port))
		);

		controls.put(\tagbox,
			StaticText(win,100@20)
			.string_(format(" Tags: %",tag))
		);
		win.view.decorator.nextLine;
	}
	//update NetAddr
	setNetAddr {|net|
		this.net=net;
		controls[\netbox].string=format("IP:  %   Port:  %  ",net.ip,net.port);
	}

	addDetector{|type,args|

		var classTmp;
		classTmp=(type++"MIDetector").asSymbol.asClass;

		if( (classTmp.notNil) && (classTmp.superclass == MIDetector) ,
			{
			detectors.put(type,classTmp.new(win,in,args));
			win.view.decorator.nextLine;
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
		controls.put(\inbox,
			StaticText(win,200@20)
			.string_(format("Listening : %",in))
		);


		win.view.decorator.nextLine;
	}

	run {	
		tempo.schedAbs(tempo.beats.ceil,{ arg beat, sec;
			if(on){	
				detectors.do({|item|
					if(item.on ){		
						item.detect(net,tag);
					};
					
				});
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