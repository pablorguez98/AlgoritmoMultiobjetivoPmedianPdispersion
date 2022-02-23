public class Nodo {

    //Atributos
    private double distanciaMinima;
    private double distanciaMaxima;

    //Constructor
    public Nodo(){
        this.distanciaMinima = Double.MAX_VALUE;
        this.distanciaMaxima = Double.MIN_VALUE;
    }

    //Get y Set
    public double getDistanciaMinima() {
        return this.distanciaMinima;
    }

    public double getDistanciaMaxima() {
        return this.distanciaMaxima;
    }

    public void setDistanciaMinima(double distanciaMinima){
        this.distanciaMinima = distanciaMinima;
    }

    public void setDistanciaMaxima(double distanciaMaxima) {
        this.distanciaMaxima = distanciaMaxima;
    }
}
