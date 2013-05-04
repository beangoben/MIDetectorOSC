DetectorManager {
	var win,tag,tempo,detectors,controls,on,in,<> net;
	
	
	*new{|win,types,tag,freq,net,in|
		^super.newCopyArgs(win,tag).init(types,freq,in,net)
	}	
	
	init {|types,freq,in,net|
		this.net=net;
		win.view.decorator.nextLine;
		tempo=TempoClock.new(freq);	
		on=true;
		detectors=();
		controls=(
				  onOff:
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
		,
				  in:EZNumber(win,80@20,"In : ",\audiobus,{|ez| in=ez.value;
					  		detectors.do({|item|
							item.in=in;
							});
					  
					  },in,labelWidth:40),
				  tagBox:StaticText(win,75@20)
				  .string_(format(" Detect: [ % ]",tag))
				  
				  );
		
		types.do({|item|
			win.view.decorator.nextLine;
			switch (item,
					\amp, { detectors.put(item,AmpDetector(win,in))},
					\freq, {detectors.put(item,FreqDetector(win,in))},
					\onset,  {detectors.put(item,OnsetDetector(win,in))},
					\power,	{detectors.put(item,PowerDetector(win,in))}
					);
					
		});
		
		
		this.run;
		
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