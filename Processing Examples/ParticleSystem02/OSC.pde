import oscP5.*;
import netP5.*;
import java.nio.*;

//OSC MESSAGE DECLARATION
OscP5 oscP5;

boolean printOSCMessage = false;

float freq;
float amp;
boolean onset;
int onsetCount;

void oscSetup() {
  //oSC INICIALIZATION  
  oscP5 = new OscP5(this, 32000);

  //CONECT THE MESSAGESS INCOMMING FORM SUPERCOLLIDER 
  //TO A FUNTION IN PROCESSING
  oscP5.plug(this, "ampResponse", "/amp");
  oscP5.plug(this, "onsetResponse", "/onset");
  oscP5.plug(this, "pitchResponse", "/pitch");
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
  this.amp = map(amp, 0, 1, 0, 1.0);
  if (printOSCMessage)println("Amp:" + id + " " + amp);
}

public void onsetResponse(int id) {
  /*if (onsetCount >= 3) {
   onset = true;
   onsetCount= 0;
   }
   onsetCount++;
   */

  onset = true;
  if (printOSCMessage)println("Onset:" + id);
}

public void pitchResponse(int id, float pitch) {
  freq = log(pitch + 1.0)*7;
  if (printOSCMessage)println("Pitch:" + id + " " + pitch);
}

