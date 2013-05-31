import oscP5.*;
import netP5.*;
import java.nio.*;

//OSC MESSAGE DECLARATION
OscP5 oscP5;

boolean printOSCMessage = false;

int freq;
float amp;

float ampStep;
float freqStep;

boolean onset;

void oscSetup() {
  //oSC INICIALIZATION  
  oscP5 = new OscP5(this, 32000);
  
  //CONECT THE MESSAGESS INCOMMING FORM SUPERCOLLIDER 
  //TO A FUNTION IN PROCESSING
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

public void pitchResponse(int id, float pitch) {
  freq = ceil(pitch);
  if (printOSCMessage)println("Pitch:" + id + " " + pitch);
}
