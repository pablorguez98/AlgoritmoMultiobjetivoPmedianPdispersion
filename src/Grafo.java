import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

public class Grafo {

    //Atributos
    private int nodos;
    private int instalaciones;
    private double[][] distancias;
    private Solucion mejor_solucion;
    private Nodo[] puntosInteres;
    private ArrayList<ArrayList<Integer>> conjuntoInstalaciones = new ArrayList<>();
    private double distanciaMinimaGeneral;
    private double distanciaMaximaGeneral;
    private double sumatorioDistanciasMinimas;
    private double sumatorioDistanciasMaximas;
    private ArrayList<Solucion> soluciones = new ArrayList<>();
    ArrayList<Solucion> solucionesBorradas = new ArrayList<>();

    //Constructor
    public Grafo(int nodos, int instalaciones){
        this.nodos = nodos;
        this.instalaciones = instalaciones;
        this.distancias = new double[nodos][nodos];
        this.puntosInteres = new Nodo[nodos];
        for(int i = 0; i < nodos; i++) {
            this.puntosInteres[i] = new Nodo();
            for (int j = 0; j < nodos; j++) {
                if (i == j)
                    this.distancias[i][j] = 0;
                else
                    this.distancias[i][j] = Double.MAX_VALUE;
            }
        }
    }

    //Get y set
    public ArrayList<Solucion> getSoluciones() {
        return this.soluciones;
    }

    //Metodos
    public void meterDistancia(int nodoA, int nodoB, double distancia){
        this.distancias[nodoA][nodoB] = distancia;
        this.distancias[nodoB][nodoA] = distancia;
    }

    public void calcularDistancias(){
        for(int i = 0; i < this.nodos; i++)
            for(int j = 0; j < this.nodos; j++)
                for(int k = 0; k < this.nodos; k++)
                    this.distancias[i][j] = Math.min(this.distancias[i][j], this.distancias[i][k] + this.distancias[k][j]);
    }

    public void calcularInstalaciones(){
        Random random = new Random(13);
        for(int i = 0; i < 1000; i++){
            boolean[] metido = new boolean[this.nodos];
            ArrayList<Integer> combinacionInstalaciones = new ArrayList<>();
            for(int j = 0; j < this.instalaciones; j++){
                int nodoInstalacion = random.nextInt(this.nodos);
                if(!metido[nodoInstalacion]){
                    combinacionInstalaciones.add(nodoInstalacion);
                    metido[nodoInstalacion] = true;
                }else
                    j--;
            }
            this.conjuntoInstalaciones.add(combinacionInstalaciones);
        }
    }

    private void calcularDistanciasNodos(ArrayList<Integer> combinacionInstalaciones){
        this.distanciaMinimaGeneral = Double.MAX_VALUE;
        this.distanciaMaximaGeneral = Double.MIN_VALUE;
        this.sumatorioDistanciasMinimas = 0;
        this.sumatorioDistanciasMaximas = 0;
        HashSet<Integer> copiaCombinacionInstalaciones = new HashSet<>(combinacionInstalaciones);
        for(int i = 0; i < this.nodos; i ++)
            if(!copiaCombinacionInstalaciones.contains(i)){
                for(int j = 0; j < this.nodos; j++)
                    if(i != j && !copiaCombinacionInstalaciones.contains(i)){
                        if(Double.compare(this.distanciaMinimaGeneral, this.distancias[i][j]) > 0)
                            this.distanciaMinimaGeneral = this.distancias[i][j];
                        if(Double.compare(this.distanciaMaximaGeneral, this.distancias[i][j]) < 0)
                            this.distanciaMaximaGeneral = this.distancias[i][j];
                        if(Double.compare(this.puntosInteres[i].getDistanciaMinima(), this.distancias[i][j]) > 0)
                            this.puntosInteres[i].setDistanciaMinima(this.distancias[i][j]);
                        if(Double.compare(this.puntosInteres[i].getDistanciaMaxima(), this.distancias[i][j]) < 0)
                            this.puntosInteres[i].setDistanciaMaxima(this.distancias[i][j]);
                    }
                this.sumatorioDistanciasMinimas = this.sumatorioDistanciasMinimas + this.puntosInteres[i].getDistanciaMinima();
                this.sumatorioDistanciasMaximas = this.sumatorioDistanciasMaximas + this.puntosInteres[i].getDistanciaMaxima();
            }
    }

    private double calcularPmedian(ArrayList<Integer> instalaciones){
        double pmedian = 0;
        for(int i = 0; i < this.nodos; i++)
            if(!instalaciones.contains(i)){
                double distanciaMinima = Double.MAX_VALUE;
                for(int j = 0; j < this.instalaciones; j++)
                    distanciaMinima = Math.min(this.distancias[i][instalaciones.get(j)], distanciaMinima);
                pmedian = pmedian + distanciaMinima;
            }
        return pmedian;
    }

    private double calcularPdispersion(ArrayList<Integer> instalaciones){
        double pdispersion = Double.MAX_VALUE;
        for(int i = 0; i < this.instalaciones; i++)
            for(int j = i+1; j < this.instalaciones; j++)
                pdispersion = Math.min(pdispersion, this.distancias[instalaciones.get(i)][instalaciones.get(j)]);
        return pdispersion;
    }

    private void normalizarSolucion(Solucion solucion){
        double pmedian = solucion.getPmedian();
        double pdispersion = solucion.getPdispersion();
        solucion.setPmedian((pmedian-this.sumatorioDistanciasMinimas)/(this.sumatorioDistanciasMaximas-this.sumatorioDistanciasMinimas));
        solucion.setPdispersion((pdispersion - this.distanciaMinimaGeneral)/(this.distanciaMaximaGeneral-this.distanciaMinimaGeneral));
    }

    private double normalizarPmedian(Solucion solucion){
        double pmedian = solucion.getPmedian();
        return (pmedian-this.sumatorioDistanciasMinimas)/(this.sumatorioDistanciasMaximas-this.sumatorioDistanciasMinimas);
    }

    private double normalizarPdispersion(Solucion solucion){
        double pdispersion = solucion.getPdispersion();
        return (pdispersion - this.distanciaMinimaGeneral)/(this.distanciaMaximaGeneral-this.distanciaMinimaGeneral);
    }

    private boolean meterSolucion(Solucion solucion){
        this.solucionesBorradas.clear();
        double pmedian2 = normalizarPmedian(solucion);
        double pdispersion2 = normalizarPdispersion(solucion);
        for(Solucion s: this.soluciones){
            double pmedian1 = normalizarPmedian(s);
            double pdispersion1 = normalizarPdispersion(s);
            if(Double.compare(pmedian1,pmedian2) <= 0 && Double.compare(pdispersion1,pdispersion2) >= 0)
                return false;
            else if(Double.compare(pmedian1,pmedian2) >= 0 && Double.compare(pdispersion1,pdispersion2) <= 0)
                this.solucionesBorradas.add(s);
        }
        for(Solucion borrar: this.solucionesBorradas)
            this.soluciones.remove(borrar);
        this.soluciones.add(solucion);
        return true;
    }

    public long frentePareto(){
        long inicioEjecucion = new Date().getTime();
        for(ArrayList<Integer> instalaciones: this.conjuntoInstalaciones){
            calcularDistanciasNodos(instalaciones);
            double pmedian = calcularPmedian(instalaciones);
            double pdispersion = calcularPdispersion(instalaciones);
            Solucion solucion = new Solucion(pmedian, pdispersion, instalaciones);
            boolean solucionMetida = meterSolucion(solucion);
            if(solucionMetida &&
                    (this.mejor_solucion == null || this.soluciones.size() == 1 ||
                            (Double.compare(this.mejor_solucion.getPdispersion()+this.mejor_solucion.getPmedian(),solucion.getPdispersion()+solucion.getPmedian())<0)))
                this.mejor_solucion = solucion;
        }
        return new Date().getTime() - inicioEjecucion;
    }

    public long busquedaLocal(){
        boolean[] esInstalacion = new boolean[this.nodos];
        for(Integer i: this.mejor_solucion.getInstalaciones())
            esInstalacion[i] = true;
        boolean frenteParetoMejorado = true;
        long inicioEjecucion = new Date().getTime();
        while(frenteParetoMejorado){
            frenteParetoMejorado = false;
            for(int i = 0; i < this.instalaciones && !frenteParetoMejorado; i++)
                for(int j = 0; j < this.nodos && !frenteParetoMejorado; j++)
                    if(!esInstalacion[j]){
                        ArrayList<Integer> instalaciones = (ArrayList<Integer>) this.mejor_solucion.getInstalaciones().clone();
                        instalaciones.remove(i);
                        instalaciones.add(j);
                        calcularDistanciasNodos(instalaciones);
                        double pmedian = calcularPmedian(instalaciones);
                        double pdispersion = calcularPdispersion(instalaciones);
                        Solucion nuevaMejorSolucion = new Solucion(pmedian, pdispersion, instalaciones);
                        frenteParetoMejorado = meterSolucion(nuevaMejorSolucion);
                        if(frenteParetoMejorado){
                            esInstalacion[this.mejor_solucion.getInstalaciones().get(i)] = false;
                            esInstalacion[j] = true;
                            this.mejor_solucion = nuevaMejorSolucion;
                        }
                    }
        }
        return new Date().getTime() - inicioEjecucion;
    }
}
