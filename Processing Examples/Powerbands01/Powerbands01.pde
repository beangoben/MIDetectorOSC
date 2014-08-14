float smoothStep = 0.05;
int stepX;

float addRadius;

int bandsNum =64;
float[] bandsValues;

void setup() {
  size(1024, 768);
  smooth(8);

  oscSetup();

  bandsValues = new float[bandsNum];
}



void draw() {
  noStroke();
  fill(255, 60);
  rect(0, 0, width, height);


  ellipseMode(RADIUS);
  translate(width/2, height/2);

  if (onset)
    fill(100 + amp*100, amp*150, amp*10);
  else {
    stroke(0);
    strokeWeight(amp*50 + 1.0);
  }
  beginShape();
  if (onset) {
    for (float i = 0; i <= TWO_PI; i+=0.098) {
      float x2 = 200 * cos( i ) * bandsValues[ceil(i)];
      float y2 = 200 * sin( i ) * bandsValues[ceil(i)];

      stroke(0);

      vertex(x2, y2);
    }
    onset = false;
  } 
  else {
    for (float i = 0; i <= TWO_PI; i+=0.098) {
      float x2 = 200 * cos( i )*bandsValues[ceil(i)];
      float y2 = 200 * sin( i )*bandsValues[ceil(i)];

      stroke(0);
      vertex(x2, y2);
    }
  }
  vertex(200 * cos( TWO_PI ), 
  200 * sin( TWO_PI ));

  vertex(200 * cos( 0 ), 
  200 * sin( 0 ));

  endShape();
}




void keyPressed() {
  if (key == 'a') {
    printOSCMessage = !printOSCMessage;
  }
}

