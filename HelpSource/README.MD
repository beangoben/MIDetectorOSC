# MIDetectorOSC

Easy to use classes for sending musical information to other applications via OSC messages.

Convenience, Musical Information, send musical information to other applications in real time.

- Amplitude
- Pitch
- Tarini
- Onsets
- FFTSubbandPower


Benjamin Sanchez Lengeling

Thanks
Thomas Sanchez Lengeling

-----

##EXAMPLES

Once installed the library into Supercollider you are ready to start detection musical information!.

First boot up the server and run a simple MIDector, just to make sure that the installation worked.

```javascript
s.boot;
(
  a=MIDetectorManager.new(net:NetAddr("127.0.0.1",32000));
)
```

This simple detector sends the information to a local host a.k.a your computer with a port number 32000.

So lest start en detection some more usefull things, like Pitch, Onsets and amplitud.

```javascript
(
a=MIDetectorManager.new(net:NetAddr("127.0.0.1",32000));
a.addDetector("Pitch");
a.addDetector("Onset",[\tol,0.3]);
a.addDetector("Amp");
)
```

- Pitch, Detection Pitch of the sound
- Onset, With a initial Threahold of 0.3, change this values to sensitivity of the Onsets
- Amplitud, Detection Amplitud.


If you want to detect Power Bands, you can just add it in the MIDetector, just like the Pitch, Onset and Amp examples.

```javascript
a.addDetector("Power",[\nbands,64]);
```

Try the detection with the following sounds.

```javascript
x={Decay2.ar(Impulse.ar(2),0.01,0.2)*SinOsc.ar(LFNoise0.kr(2).range(20,10000).poll)}.play
//Or your mouse
x={Decay2.ar(Impulse.ar(2),0.01,0.2)*SinOsc.ar(MouseX.kr(20,10000,1).poll)}.play
//White noise
x={WhiteNoise.ar}.play
//Kill it!
x.free
```

#### Processing Examples

Processing code to recieve and parse messages.
The processing example uses the library [oscP5](http://www.sojamo.de/libraries/oscP5/), it can be installed on processing using the Library Manager or manual installation.

For example, if you use the following MIDector configuration, you are going to receive in processing 4 different types of information pitch, onset, amplitud and power bands.

```javascript
(
a=MIDetectorManager.new(net:NetAddr("127.0.0.1",32000));
a.addDetector("Pitch");
a.addDetector("Onset",[\tol,0.3]);
a.addDetector("Amp");
a.addDetector("Power",[\nbands,64]);
)
```

Base on this configuration, in processing you plug OSC functions that will receive that musical information, you cand do this for all the Detectors.

```java
  oscP5.plug(this, "ampResponse", "/amp");
  oscP5.plug(this, "onsetResponse", "/onset");
  oscP5.plug(this, "pitchResponse", "/pitch");
  oscP5.plug(this, "powerResponse", "/power");
```

Complete Example in processing.

```java
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
```
