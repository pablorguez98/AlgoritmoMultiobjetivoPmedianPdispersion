public class Solucion {
    //Atributos
    private double pmedian;
    private double pmedianNormalizada;
    private double pdispersion;
    private double pdispersionNormalizada;
    private boolean[] instalaciones;

    //Constructor
    public Solucion(double pmedian, double pdispersion, boolean[] instalaciones){
        this.pmedian = pmedian;
        this.pdispersion = pdispersion;
        this.instalaciones = instalaciones;
    }

    //Get y Set
    public double getPmedian() {
        return this.pmedian;
    }

    public double getPdispersion() {
        return this.pdispersion;
    }

    public boolean[] getInstalaciones() {
        return this.instalaciones;
    }

    public void setPmedian(double pmedian) {
        this.pmedian = pmedian;
    }

    public void setPdispersion(double pdispersion) {
        this.pdispersion = pdispersion;
    }

    public double getPdispersionNormalizada() {
        return this.pdispersionNormalizada;
    }

    public double getPmedianNormalizada() {
        return this.pmedianNormalizada;
    }

    public void setPmedianNormalizada(double pmedianNormalizada) {
        this.pmedianNormalizada = pmedianNormalizada;
    }

    public void setPdispersionNormalizada(double pdispersionNormalizada) {
        this.pdispersionNormalizada = pdispersionNormalizada;
    }
}
