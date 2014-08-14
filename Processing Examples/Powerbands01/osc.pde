import oscP5.*;
import netP5.*;
import java.nio.*;

//OSC MESSAGE DECLARATION
OscP5 oscP5;

boolean printOSCMessage = false;

float freq;
float amp;

float ampStep;
float freqStep;

boolean onset;

void oscSetup() {
  //oSC INICIALIZATION  
  oscP5 = new OscP5(this, 32000);

  //CONECT THE MESSAGESS INCOMMING FORM SUPERCOLLIDER 
  //TO A FUNTION IN PROCESSING
  oscP5.plug(this, "ampResponse", "/amp");
  oscP5.plug(this, "onsetResponse", "/onset");
  oscP5.plug(this, "pitchResponse", "/pitch");
  oscP5.plug(this, "powerResponse", "/powerbands");
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
  this.amp = amp;
  if (printOSCMessage)println("Amp:" + id + " " + amp);
}

public void onsetResponse(int id, float num) {
  onset = true;
  if (printOSCMessage)println("Onset:" + id);
}

public void pitchResponse(int id, float pitch) {
  freq = pitch;
  if (printOSCMessage)println("Pitch:" + id + " " + pitch);
}

public void powerResponse(int id, int nbands, byte[] data) {
  if (printOSCMessage)println("Power:" + id + " " + nbands);

  //Parse bytes to floats
  int offset=data.length-nbands*4;    
  ByteBuffer buffer = ByteBuffer.allocate(nbands*4);
  buffer.put(data, 0, 4);
  buffer.put(data, 4+offset, 4*(nbands-1)); //skip padding

  //  if(printOSCMessage){
  println("[");
  for (int i=0; i < nbands; i++) {
    print(buffer.getFloat(i*4)+",");
    bandsValues[i] = buffer.getFloat(i*4);
  }  
  println("]");
  //}
  println(nbands);
  bandsNum = nbands;
}

