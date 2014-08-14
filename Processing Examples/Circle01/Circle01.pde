void setup(){
  size(1024, 768);
  smooth(8);
  colorMode(HSB);
  oscSetup();
}



void draw(){
  noStroke();
  fill(0, 20);
  rect(0, 0, width, height);
  
  if(onset){
   fill(freq*200, 255, 255);
   ellipse(width/2, height/2, 40 + amp*50, 40 + amp*50);
   onset = false; 
  }
}


void keyPressed(){
  if(key == 'a'){
    printOSCMessage = !printOSCMessage;
  }
}
