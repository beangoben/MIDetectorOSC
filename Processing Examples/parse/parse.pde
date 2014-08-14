import oscP5.*;
import netP5.*;
import java.nio.*;

OscP5 oscP5;

boolean printOSCMessage = true;

void setup(){
  size(100,100);
  oscSetup();
}
void draw(){

}

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

public void powerResponse(int id, int nbands, byte[] data) {
  if (printOSCMessage)println("Power:" + id + " " + nbands);

  //Parse bytes to floats
  int offset=data.length-nbands*4;    
  ByteBuffer buffer = ByteBuffer.allocate(nbands*4);
  buffer.put(data, 0, 4);
  buffer.put(data, 4+offset, 4*(nbands-1)); //skip padding

  if(printOSCMessage){
    print("[");
    for (int i=0; i < nbands; i++) {
      print(buffer.getFloat(i*4)+",");
    }  
    println("]");
 }
    
}


void keyPressed(){
  if(key == 'a'){
    printOSCMessage = !printOSCMessage;
  }
}
