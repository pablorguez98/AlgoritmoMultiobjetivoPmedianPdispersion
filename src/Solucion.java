import java.util.ArrayList;

public class Solucion {

    //Atributos
    private double pmedian;
    private double pdispersion;
    private ArrayList<Integer> instalaciones;

    //Constructor
    public Solucion(double pmedian, double pdispersion, ArrayList<Integer> instalaciones){
        this.pmedian = pmedian;
        this.pdispersion = pdispersion;
        this.instalaciones = (ArrayList<Integer>) instalaciones.clone();
    }

    //Get y Set
    public double getPmedian() {
        return this.pmedian;
    }

    public double getPdispersion() {
        return this.pdispersion;
    }

    public ArrayList<Integer> getInstalaciones() {
        return this.instalaciones;
    }

    public void setPmedian(double pmedian) {
        this.pmedian = pmedian;
    }

    public void setPdispersion(double pdispersion) {
        this.pdispersion = pdispersion;
    }
}
