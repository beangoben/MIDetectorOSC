PowerMIDetector : MIDetector{
	
	*new{|win,in,args|
		^super.newCopyArgs(win,in).init(args);	
	}	
	
	init {|args|
		this.initValues(args);
		super.init();
		this.loadSynthDef();
		super.makeGenericGui();
		this.makeSpecificGui();
	}

	initValues {|args|
		name="Power";
		nBus=32;
		bus=Bus.control(Server.default,nBus);	
		value=0;
	}

	loadSynthDef {
		synthname=name++"MIDetect";
		SynthDef(synthname,{|in=0,gate=1,bus,mult=1|
			var buffer,sig,powers,chain;
			buffer=LocalBuf(2048);
			sig=InFeedback.ar(in);
			chain=FFT(buffer,sig);
			powers = FFTSubbandPower.kr(chain,[ 55, 65.40639132515, 77.78174593052, 92.498605677909, 110, 130.8127826503, 155.56349186104, 184.99721135582, 220, 261.6255653006, 311.12698372208, 369.99442271163, 440, 523.2511306012, 622.25396744416, 739.98884542327, 880, 1046.5022612024, 1244.5079348883, 1479.9776908465, 1760, 2093.0045224048, 2489.0158697766, 2959.9553816931, 3520, 4186.0090448096, 4978.0317395533, 5919.9107633862, 7040, 8372.0180896192, 9956.0634791066, 11839.821526772 ]);
			Out.kr(bus, powers*mult);
		}).load(Server.default);
	}

	makeSpecificGui {
		/*
		controls.put(\mult,
			EZSlider(win,30@50,nil,[0.01,100,\exp,0.01].asSpec,
			{|ez|synth.set(\mult,ez.value) },
			1,false,numberWidth:25,layout:\vert)
			);
		*/
		controls.put(\show,
			MultiSliderView(win, Rect(0, 0, 250,50))
			.value_(0.dup(nBus))
			.size_(nBus)
			.drawLines_(true)
			.drawRects_(false)
			.indexThumbSize_(250/nBus)
			);	
		win.setInnerExtent(win.bounds.width,win.bounds.height+54);
	}
	
	detect {|net,tag|
		bus.getn(nBus,{|val|
			val=val.max(0).min(1);
			if(verbose){format("% :  % ",name,val).post};
			{controls[\show].value_(val)}.defer;
			net.sendMsg(oscstr,tag,val[0],val[1],val[2],val[3],val[4],val[5],val[6],val[7],
			val[8],val[9],
			val[10],val[11],val[12],val[13],val[14],val[15],val[16],val[17],val[18],val[19],
			val[20],val[21],val[22],val[23],val[24],val[25],val[26],val[27],val[28],val[29],
			val[30],val[31]);
			});
		}	
	
}