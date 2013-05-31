void setup(){
  size(1024, 768);
  smooth(8);
  
  oscSetup();
}



void draw(){
  noStroke();
  fill(0, 20);
  rect(0, 0, width, height);
  
  if(onset){
   fill(amp*50 + freq*200, 200 - freq*200, 220 - freq*200);
   ellipse(width/2, height/2, 40 + amp*50, 40 + amp*50);
   onset = false; 
  }
}


void keyPressed(){
  if(key == 'a'){
    printOSCMessage = !printOSCMessage;
  }
}
