import oscP5.*;
import netP5.*;
import java.nio.*;

//OSC MESSAGE DECLARATION
OscP5 oscP5;

boolean printOSCMessage = false;

void oscSetup() {
  //oSC INICIALIZATION  
  oscP5 = new OscP5(this, 32000);

  //CONECT THE MESSAGESS INCOMMING FORM SUPERCOLLIDER 
  //TO A FUNTION IN PROCESSING
  oscP5.plug(this, "FTCrestResponse", "/ftcrest");
  oscP5.plug(this, "flatResponse", "/flat");
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

public void FTCrestResponse(int id, float ftcrest){
  if(!usingFlat){
    noisyness = map(ftcrest, 2, 7, 0.8, 0.005);
  }
}

public void SpecflatResponse(int id, float specflat){
  if(usingFlat){
    noisyness = map(specflat, 0, 1, 0.05, 1.0);
  }
}


