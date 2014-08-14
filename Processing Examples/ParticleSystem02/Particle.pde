class Particle {
  float posx;  //posicion X
  float posy;  //posicion Y
  float dirX;
  float dirY;
  float speedX;
  float speedY;

  float tetaX; //angulo en radianes del movimiento circular
  float tetaY;

  float  tam;  //tamano de la particula

  float xoff =0.001;

  /*Valores de color para la particula
   se sugiere cambiar estos valores en drawEllipse() o drawRect() y setTone() al final de este codigo
   */
  float ton = 0.0;
  float transparencia = 55;
  float saturacion = 150;
  float brillo = 170;

  /*
    Valores iniciales de cada una de las particulas
   aqui se puden modificar los valores iniciales de las particulas
   para los valores de tetaX y tetaY es el angulo en radianes de inclinacion para
   el movimiento circular de la particula
   */
  Particle(float posx, float posy, float tam ) {
    this.posx = posx;
    this.posy = posy;
    this.tam = tam;
    dirX = random(-3, 3);
    dirY = random(-3, 3);
    speedX = random(1.2); 
    speedY = random(1.2);
    tetaX = random(0.4, 0.9);
    tetaY = random(0.4, 0.9);
  }

  /*
    movimiento de la particula en linea recta
   */
  void moveLine() {
    posx = posx + dirX*speedX;
    posy = posy + dirY*speedY;
  }
  /*
   puden cambiar las funciones de seno y coseno
   para darse una idea como se pude compratar una particula.
   un cambio puede ser
   posx = posx + dirX * speedX * sin(tetaX);
   posy = posy + dirY * speedY * sin(tetaY); 
   las funciones que pueden cambiar 
   sin(teta) asin(teta) inverso de seno
   cos(teta) acos(teta) inverso de coseno
   tan(teta) atan(teta) inverso de tangete
   */
  void moveCircular() {
    posx = posx + dirX * speedX * cos(tetaX);
    posy = posy + dirY * speedY * sin(tetaY);
    tetaX=tetaX + 0.009;
    tetaY=tetaY + 0.005;
  }

  /*
  simulacion del rebote de la particula
   solamente cambia la direecion dependiendo del rebote
   */
  void rebotar() {
    if ( posx > width) {
      dirX = dirX*(-1);
    }

    if ( posx < 0) {
      dirX = dirX*(-1);
    }

    if (posy > height) {
      dirY = dirY*(-1);
    }

    if (posy < 0) {
      dirY = dirY*(-1);
    }
  }

  /*
  simulacion del rebote tipo juego asteroides
   la particula traspasa la pantalla y depues se va al sentido contrario de la pantalla
   */
  void pass() {
    if ( posx  > width) {
      posx = tam/2;
    }

    if ( posx < 0) {
      posx = width - tam/2;
    }

    if (posy > height) {
      posy = tam/2;
    }

    if (posy < 0) {
      posy = height - tam/2;
    }
  }

  /*
  Pone el tono a la Particula
   se puede cambiar los valores de random
   */
  float getTone() {
    return ton = (ton + amp*15) % 255;  //cambiar el 0.7 para diferentes colores
  }

  /*  
   Dibuja la Particula
   */

  void drawEllipse() {
    fill( 0, 255, 0);//setTone(), saturacion, brillo, transparencia + random(-25, 25));
    ellipse(posx, posy, tam, tam);
  }

  //Dibuja una particula y se comporta como un beat se hace mas grande o mas pequeno
  void drawBeat() {
    //codigo para que la particula cambie de tamano
    //noise se comporta casi igual que random() pero sigue una secuecias 
    float n = noise(xoff)*tam*1.5;   //cambiar el 1.5 para que la particula sea mas grande o pequenas

    //color de contorno a la particula
    //stroke(setTone(), saturacion, brillo, transparencia + random(-25, 25));
    //codigo aparesca el tamano del contorno de la  particula aqui se puede anadir un random random(1, 10)
    //strokeWeight(5);

    fill( getTone(), saturacion, brillo, transparencia);    
   // ellipse(posx, posy, n, n);
    ellipse(posx, posy, n+ freq,  n+ freq);
    xoff+=0.02;  //que tan rapido se hace grande o pequeno
  }

  /*
  dibuja la particula como un rectangulo
   */
  void drawRect() {
    rect(posx, posy, tam, tam);
  }
}

