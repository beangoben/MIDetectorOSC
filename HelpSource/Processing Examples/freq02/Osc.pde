import oscP5.*;
import netP5.*;

//OSC MESSAGE DECLARATION
OscP5 oscP5;

boolean printOSCMessage = false;

float freq;
float onset = 1.0;

void oscSetup() {
  //oSC INICIALIZATION  
  oscP5 = new OscP5(this, 32000);

  //CONECT THE MESSAGESS INCOMMING FORM SUPERCOLLIDER 
  //TO A FUNTION IN PROCESSING
  oscP5.plug(this, "pitchResponse", "/pitch");
  oscP5.plug(this, "onsetResponse", "/onset");
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

public void pitchResponse(int id, float pitch) {
  freq = map(log(pitch),log(80),log(10000),0,255);
  if (printOSCMessage)println("Pitch:" + id + " " + pitch);
}

public void onsetResponse(int id) {
  onset = 1.0;
  if (printOSCMessage)println("Onset:" + id);
}

