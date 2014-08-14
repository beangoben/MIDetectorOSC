class Line {
  float x;
  float y;

  float lheihgt;

  float nVal;

  Line(float lheihgt) {
    randPos();
    this.lheihgt = lheihgt;
    nVal         = random(0.3);
  }

  void draw(float amp) {
    stroke(255);
    float offY = noise(nVal)*amp;
    beginShape();
    vertex(x + offY, y);
    vertex(x + offY, lheihgt);
    endShape();
    
    updatePos();
  }

  void updatePos() {
    nVal += 0.02;
  }

  void randPos() {
    x = random(0, width);
    y = 0;
  }
}

