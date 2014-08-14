

PImage img;

void setup(){
  size(512,512);
  oscSetup();
 background(0);
}

void draw(){
  
}



void keyPressed(){
  if(key == 'a'){
    printOSCMessage = !printOSCMessage;
  }
}
