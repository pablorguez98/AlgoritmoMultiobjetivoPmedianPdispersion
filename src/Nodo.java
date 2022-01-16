public class Nodo {

    //Atributos de la clase Nodo
    private int numero_nodo;
    private double distancia_minima;
    private double distancia_maxima;

    //Constructor de la clase Nodo
    public Nodo(int numero_nodo){
        this.numero_nodo = numero_nodo;
        this.distancia_minima = Double.MAX_VALUE;
        this.distancia_maxima = Double.MIN_VALUE;
    }

    //Métodos get y set de la clase Nodo
    public double getDistancia_maxima() {
        return this.distancia_maxima;
    }

    public double getDistancia_minima() {
        return this.distancia_minima;
    }

    public void setDistancia_minima(double distancia_minima) {
        this.distancia_minima = distancia_minima;
    }

    public void setDistancia_maxima(double distancia_maxima) {
        this.distancia_maxima = distancia_maxima;
    }
}