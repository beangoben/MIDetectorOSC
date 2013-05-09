TITLE:: MIDetectorOSC guide/overview
summary:: Easy to use classes for sending musical information to other applications via OSC messages.
categories:: Convenience, Musical Information, OSC
related:: Classes/Amplitude, Classes/Pitch, Classes/Tarini, Classes/Onsets, Classes/FFTSubbandPower

DESCRIPTION:: Easy to use classes for sending musical information to other applications via OSC messages. Fill me more



2013 -- Benjamin Sanchez Lengeling

Thanks also to:
Thomas Sanchez Lengeling

EXAMPLES::

CODE::
s.boot;

(
//use default parameters for the detectors
a=MIDetectorManager.new(net:NetAddr("127.0.0.1",32000));
//Use default parameter for pitch
a.addDetector("Pitch");
//Change initial tolerance of onset to 0.3
a.addDetector("Onset",[\tol,0.3]);
a.addDetector("Amp");
//Choose Number of bands to detect between the default frecuency values
a.addDetector("Power",[\nbands,64]);
)

//Try it out with this sound
x={Decay2.ar(Impulse.ar(2),0.01,0.2)*SinOsc.ar(LFNoise0.kr(2).range(20,10000).poll)}.play
//Or your mouse
x={Decay2.ar(Impulse.ar(2),0.01,0.2)*SinOsc.ar(MouseX.kr(20,10000,1).poll)}.play
//White noise
x={WhiteNoise.ar}.play
//Kill it!
x.free
::

EXAMPLES::
Processing code to recieve and parse messages.

CODE::
import java.nio.*;

OscP5 oscP5;
NetAddress myRemoteLocation;


import oscP5.*;
import netP5.*;

boolean printOSCMessage = true;


void setup()
{
  size(100,100);
  oscSetup();
  background(0);
}

void draw() {

}

void oscSetup(){
  oscP5 = new OscP5(this, 32000);
  oscP5.plug(this, "ampResponse", "/amp");
  oscP5.plug(this, "onsetResponse", "/onset");
  oscP5.plug(this, "pitchResponse", "/pitch");
  oscP5.plug(this, "powerResponse", "/power");
}


/* Osc events reciever for not plugged messages */
void oscEvent(OscMessage theOscMessage) {
    /* print the address pattern and the typetag of the received OscMessage */
    if (printOSCMessage && theOscMessage.isPlugged() == false) {
        print("### received an osc message.");
        print(" addrpattern: " + theOscMessage.addrPattern());
        println(" typetag: " + theOscMessage.typetag());
        theOscMessage.printData();
    }
  
}
/* Pluggable functions */
public void ampResponse(int id, float amp) {
    if (printOSCMessage)println("Amp:" + id + " " + amp);
}

public void onsetResponse(int id) {

    if (printOSCMessage)println("Onset:" + id);
}

public void pitchResponse(int id, float pitch) {

    if (printOSCMessage)println("Pitch:" + id + " " + pitch);
}

public void powerResponse(int id, int nbands,byte[] data){
    if (printOSCMessage)println("Power:" + id + " " + nbands);

    //Parse bytes to floats
    int offset=data.length-nbands*4;    
    ByteBuffer buffer = ByteBuffer.allocate(nbands*4);
    buffer.put(data,0,4);
    buffer.put(data,4+offset,4*(nbands-1)); //skip padding
    
    println("[");
    for(int i=0; i < nbands; i++){
      print(buffer.getFloat(i*4)+",");
    }  
   println("]");
   
}
::