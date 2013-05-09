MIDetectorManager {
	var win,<>net,in,tag;
	var tempo,detectors,controls,on;
	
	
	*new{|win,types,rate,net,in,tag|
		^super.newCopyArgs(win,net,in,tag).init(types,rate)
	}	
	
	init {|types,rate|
		// Initialize stuff
		on=true;
		detectors=();
		controls=();
		//create a TempoClock for our Osc-sending loop
		tempo=TempoClock.new(rate);	
		// Create window if not given
		if(win.isNil,
			nil,
			this.makeWindow
		);
        // GUI
        this.makeNetGui;
        this.makeMainGui;
		// Create Detectors if an array with types is given
		if(types.isNil,
			nil,
			types.do({|item|
				this.addDetector(item)
			})
		);
		
		this.run;
		
	}
	
	makeWindow {
		win = Window.new("MIDetectorManager",Rect(128, 90, 400, 200)).front;
		win.view.decorator = FlowLayout( win.view.bounds,10@10, 4@4);
		win.onClose_({ });
	}

	makeNetGui {
		controls.put(\netupdate,
			Button(win,60@20)
			.states_([
            ["Update",Color.white,Color.black]
            ])
            .action_({|butt|
            controls[\netbox].string=format("IP:  %   Port:  %  ",net.ip,net.port);
            })
		);
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

	addDetector {|type|
		switch (type,
				"Amp", { detectors.put(type,AmpMIDetector(win,in))},
				"Pitch", {detectors.put(type,PitchMIDetector(win,in))},
				"Onset",  {detectors.put(type,OnsetMIDetector(win,in))},
				{ format("MIDetector Error: Could not find detector %s",type).postln;}
				);
		win.view.decorator.nextLine;
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
		tempo.clear;
		tempo.stop;	
		controls[\onOff].valueAction_(0);
		detectors.do({|item|
			item.kill;	
		});
	}
	
	
	
}