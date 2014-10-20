

PImage img;
int cur=0;
int pixelz[];
int pixn;

void setup(){
  size(512,512);
  oscSetup();
  frameRate(60);
 background(0);
 colorMode(HSB);
 noStroke();
 pixn=height*width;
 pixelz = new int[pixn];

}

void draw(){
  
}

void horzShift(int offset){
  loadPixels();
  for(int y=0;y<height;y++){
    for(int x=-1*offset;x< (width);x++){
      pixelz[(x+offset)+y*width] = pixels[x+y*width];
    }
  }
  
  for(int i=0; i < pixn; i++){
      pixels[i] = pixelz[i];
  }


  updatePixels();
}


void keyPressed(){
  if(key == 'a'){
    printOSCMessage = !printOSCMessage;
  }
}
