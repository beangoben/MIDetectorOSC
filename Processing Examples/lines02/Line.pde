class Line {
  float x;
  float y;

  float lheihgt;

  float nVal;
  float nOffX;

  boolean activateLine;

  int lineColor= 255;
  Line(float lheihgt) {
    randPos();
    this.lheihgt = lheihgt;
    nVal         = random(0.3);
  }

  void draw() {
    if (activateLine) {
      stroke(lineColor);
      beginShape();
      vertex(x + nOffX, y);
      vertex(x + nOffX, lheihgt);
      endShape();
      update();
    }
  }


  void update() {
    nOffX = noise(nVal)*35;
    nVal += 0.02;

    if (lineColor > 0)
      lineColor -= 1.6;
  }

  void activate() {
    activateLine = true;
    lineColor    = 255;
    randPos();
  }

  void randPos() {
    x = random(0, width);
    y = 0;
  }
}

