Vector particulas;
/*
Manege de todas las particulas como un sistema de particulas...
*/
class PhysicsSystem{

  PhysicsSystem(){
    particulas = new Vector();
  }
  
  /*
    crea una particula en una posicion aleatoria de la pantalla
    Cambiar el ultimo valor random para obtener particulas mas grandes o pequenas
  */
  void createParticula(){
    particulas.add( new Particle( random(width - 20), random(height - 20), random(9*tamI, 33*tamI)));
  }
  
  /*
  crea muchas particulas
  */
  void createAll(){
    for(int i = 0; i < nP; i++)
      particulas.add( new Particle( random(width - 20), random(height - 20), random(7*tamI, 33*tamI)));
  }
  
  //elimina las particulas
  void removeParticula(){
    if( particulas.size() > 0)
      particulas.remove( particulas.size() - 1);
  }
  
  /*
  aqui donde se puede cambiar como se mueven, rebotan y se dibujan las particulas
  */
  void dibujar(int index){
    movment('c', index);   //movimiento 
    bounce('r', index);    //rebotar
    drawMode('b', index);  //modo de dibujar
  }
  
  /*
  cambiar el movimiento de la particula
  l  para un movimiento lineal
  c  para un movimiento circular
  t  para un movimiento lineal y circular
  */
  void movment(char mode, int index){
    switch(mode){
    case 'l':
      ((Particle)particulas.get(index)).moveLine();
      break;
    case 'c':
      ((Particle)particulas.get(index)).moveCircular();
      break;
    case 't':
      if(index %2 ==0)
        ((Particle)particulas.get(index)).moveLine();
      else
        ((Particle)particulas.get(index)). moveCircular();
      break;
    }
  }
  /*
  Cambio de reboto o no
  r para que la particula rebote si toca la pantalla
  p para que la particula se traslade a la parte contraria de la pantalla
    como el juego de asteroides
  */
  void bounce(char mode, int index){
    switch(mode){
    case 'r':
      ((Particle)particulas.get(index)).rebotar();
      break;
    case 'p':
      ((Particle)particulas.get(index)).pass();
      break;    
    }  
  }
  /*
  Cambia como dibuja las particulas en la pantalla
  e para el tipo de particula sea ellipse
  r para el tipo de particula sea rectangulo
  b para que la particula se comparte como un beat
  t para que el tipo de particulas sea elatorias rectangulares y ellipticas
  */
  void drawMode(char mode, int index){
    switch(mode){
    case 'e':
      ((Particle)particulas.get(index)).drawEllipse();
      break;
    case 'r':
      ((Particle)particulas.get(index)).drawRect();
      break;
    case 'b':
      ((Particle)particulas.get(index)).drawBeat();
      break;
    case 't':
      if( index%2 == 0 )
        ((Particle)particulas.get(index)).drawEllipse();
      else 
        ((Particle)particulas.get(index)).drawRect();
      break; 
    }
  }
  
  /*
  Cambia de posicion si la posicion es negativa entonces
  no se toma encuenta, si nadamas quieres cambiar la posicion en x
  solamente se pone y en negativa. se hace lo mismo para y.
  */
  void setPos(int index, float x, float y){
    if( y >= -1){
      ((Particle)particulas.get(index)).posx = x;
    }
    if( x >= -1){
      ((Particle)particulas.get(index)).posy = y;
    }
    if( x >=0 & y  >= 0){
      ((Particle)particulas.get(index)).posx = x;
      ((Particle)particulas.get(index)).posx = y;
    }
  }
  
}










