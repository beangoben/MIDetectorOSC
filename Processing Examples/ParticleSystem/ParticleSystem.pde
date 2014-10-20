import java.util.Vector;
import javax.media.opengl.GL2;
import promidi.*;
import java.nio.*;

PhysicsSystem fisica;

int nP = 10;  //numero de particulas cuando se presiona la tecla c
float tamI = 1; //cambiar para que las particulas sean mas grandes 0 pequenas
int strokeSize = 1;


int backg = 0;
int backW = 0;//#ffffff;

void setup() {
  size(1024, 900);
  
  //OSC
  oscSetup();
  
  smooth(8);
  noCursor();

  colorMode(HSB, 255); 

  fisica = new PhysicsSystem();
}


void draw() {
  colorMode(RGB);
  fill(0, 5);
  rect(0, 0, width, height);
 
  colorMode(HSB);
  strokeWeight(strokeSize);
  noStroke();

  for (int i = 0; i < particulas.size(); i++) {
    fisica.dibujar(i);
  }
  
  if(onset){
    fisica.createParticula();
    onset = false;
  }
  
}

