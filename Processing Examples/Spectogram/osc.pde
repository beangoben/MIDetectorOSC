import oscP5.*;
import netP5.*;
import java.nio.*;

OscP5 oscP5;

boolean printOSCMessage = false;

void oscSetup() {
  //Osc Initialization
  OscProperties properties = new OscProperties();
  properties.setDatagramSize(1024*8);
  properties.setListeningPort(32000);
  oscP5 = new OscP5(this,properties);
  
  //CONECT THE MESSAGESS INCOMMING FORM SUPERCOLLIDER 
  //TO A FUNTION IN PROCESSING
   oscP5.plug(this, "ftmagsResponse", "/ftmags");
}

/* Osc events reciever for non plugged messages */
void oscEvent(OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
  if (printOSCMessage && theOscMessage.isPlugged() == false) {
    print("### received an osc message.");
    print(" addrpattern: " + theOscMessage.addrPattern());
    println(" typetag: " + theOscMessage.typetag());
  }
}


public void ftmagsResponse(int id, int n, byte[] data) {
  if (printOSCMessage)println("FTmags:" + id + " " + n);

  //Parse bytes to floats
  int offset=data.length-n*4;    
  ByteBuffer buffer = ByteBuffer.allocate(n*4);
  buffer.put(data, 0, 4);
  buffer.put(data, 4+offset, 4*(n-1)); //skip padding

  if(printOSCMessage){
    print("[");
    for (int i=0; i < n; i++) {print(buffer.getFloat(i*4)+",");}  
    println("]");
  }
    
    for (int i=0; i < n; i++) {
     set(width-1,i,color(buffer.getFloat(i*4)*255,255,255));
    }  
    horzShift(-1);
}
