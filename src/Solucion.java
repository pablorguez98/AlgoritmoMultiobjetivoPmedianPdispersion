import java.util.ArrayList;

public class Solucion {

    //Atributos de la clase Solucion
    private double pmedian;
    private double pdispersion;
    private ArrayList<Integer> combinacion_instalaciones;

    //Contructor de la clase Solucion
    public Solucion(double pmedian, double pdispersion, ArrayList<Integer> combinacion_instalaciones){
        this.pmedian = pmedian;
        this.pdispersion = pdispersion;
        this.combinacion_instalaciones = combinacion_instalaciones;
    }

    //Métodos get y set de la clase Solucion
    public double getPmedian(){
        return this.pmedian;
    }

    public double getPdispersion() {
        return this.pdispersion;
    }

    public ArrayList<Integer> getCombinacion_instalaciones(){
        return this.combinacion_instalaciones;
    }

    public void setPmedian(double pmedian) {
        this.pmedian = pmedian;
    }

    public void setPdispersion(double pdispersion) {
        this.pdispersion = pdispersion;
    }
}