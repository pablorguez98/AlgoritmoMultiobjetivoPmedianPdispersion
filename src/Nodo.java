public class Solucion {
    //Atributos de la clase Solucion.
    private final double pmedian, pdispersion, pmedianNormalizado, pdispersionNormalizado;
    private final boolean[] instalaciones;

    //Constructor de la clase Solucion.
    public Solucion(double pmedian, double pdispersion, double pmedianNormalizado, double pdispersionNormalizado, boolean[] instalaciones){
        this.pmedian = pmedian;
        this.pdispersion = pdispersion;
        this.pmedianNormalizado = pmedianNormalizado;
        this.pdispersionNormalizado = pdispersionNormalizado;
        this.instalaciones = instalaciones.clone();
    }

    //Métodos get y set de la clase Solucion.
    //Método que devuelve el valor de la función objetivo pmedian de la solución sin normalizar.
    public double getPmedian() {
        return this.pmedian;
    }

    //Método que devuelve el valor de la función objetivo pedispersion de la solución sin normalizar.
    public double getPdispersion() {
        return this.pdispersion;
    }

    //Método que devuelve el valor de la función objetivo pmedian de la solución normalizado.
    public double getPmedianNormalizado() {
        return this.pmedianNormalizado;
    }

    //Método que devuelve el valor de la función objetivo pdispersion de la solución normalizado.
    public double getPdispersionNormalizado() {
        return this.pdispersionNormalizado;
    }

    //Método que devuelve el conjunto de instalaciones de una solución.
    public boolean[] getInstalaciones() {
        return this.instalaciones;
    }
}
