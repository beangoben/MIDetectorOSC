void setup() {
  size(1024, 768/2);
  smooth(8);
  oscSetup();

}

void draw() {
  noStroke();
  fill(155, 0, 0, 10);
  rect(0, 0, width, height);

  strokeWeight(amp*50 + 1);
  for (float i=20; i < freq; i+=20) {
    // get log scale value
    float x1 = map(log(freq), log(20), log(22000), 0, width);
    float x2 = map(freq, 20, 22000, 0, width);

    stroke(0, 30);
    line(x1, 0, x2, height);
  }

  float x1 = map(log(freq), log(20), log(22000), 0, width);
  float x2 = map(freq, 20, 22000, 0, width);
  stroke(50);
  line(x1, 0, x2, height);
}

