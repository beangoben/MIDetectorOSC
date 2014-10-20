void setup() {
  size(1024, 768/2);
  smooth(8);
  oscSetup();
}

void draw() {
  noStroke();
  fill(freq, 10);
  rect(0, 0, width, height);


  strokeWeight(2.0);
  stroke(255 - freq);
  fill(0, freq, freq+ 20);
  ellipse(width/2, height/2, 50 + onset*180, 50 + onset*180);

  if (onset > 0.0)
    onset -= 0.06;
  else
    onset=0.0;
}

