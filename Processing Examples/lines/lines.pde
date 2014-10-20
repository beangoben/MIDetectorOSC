Line linea;

void setup() {
  size(1024, 768/2);
  smooth(8);
  oscSetup();

  linea = new Line(height);
}

void draw() {
  noStroke();
  fill(0, 10);
  rect(0, 0, width, height);


  linea.draw(amp);
}

