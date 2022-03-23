import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Grafo {
    //Atributos
    private final int nodos;
    private final int instalaciones;
    private double[][] distancias;
    private Solucion mejor_solucion;
    private Nodo[] puntosInteres;
    private double distanciaMinimaGeneral;
    private double distanciaMaximaGeneral;
    private double sumatorioDistanciasMinimas;
    private double sumatorioDistanciasMaximas;
    private ArrayList<Solucion> soluciones = new ArrayList<>();
    private ArrayList<Solucion> solucionesBorradas = new ArrayList<>();

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

        for(int i = 0; i < this.nodos; i ++){
            for(int j = 0; j < this.nodos; j++)
                if (i != j) {
                    this.puntosInteres[i].setDistanciaMinima(Math.min(this.puntosInteres[i].getDistanciaMinima(), this.distancias[i][j]));
                    this.puntosInteres[i].setDistanciaMaxima(Math.max(this.puntosInteres[i].getDistanciaMaxima(), this.distancias[i][j]));
                    this.distanciaMinimaGeneral = Math.min(this.distanciaMinimaGeneral, this.puntosInteres[i].getDistanciaMinima());
                    this.distanciaMaximaGeneral = Math.max(this.distanciaMaximaGeneral, this.puntosInteres[i].getDistanciaMaxima());
                }
            this.sumatorioDistanciasMinimas = this.sumatorioDistanciasMinimas + this.puntosInteres[i].getDistanciaMinima();
            this.sumatorioDistanciasMaximas = this.sumatorioDistanciasMaximas + this.puntosInteres[i].getDistanciaMaxima();
        }
    }

    public long frentePareto(){
        double alfa = 0.0;
        Random random = new Random(13);
        ArrayList<Integer> conjuntoInstalaciones = new ArrayList<>();
        boolean[] instalaciones = new boolean[this.nodos];
        long inicioEjecucion = new Date().getTime();
        while(alfa <= 1.0){
            for(Integer n: conjuntoInstalaciones)
                instalaciones[n] = false;
            conjuntoInstalaciones.clear();
            int nodoInstalacionOrigen = random.nextInt(this.nodos);
            instalaciones[nodoInstalacionOrigen] = true;
            conjuntoInstalaciones.add(nodoInstalacionOrigen);
            for(int i = 1; i < this.instalaciones; i++){
                double funcionAgregada = Double.MIN_VALUE;
                int nuevaInstalacion = random.nextInt(this.nodos);
                for(int j = 0; j < this.nodos; j++){
                    if(instalaciones[j] == false){
                        instalaciones[j] = true;
                        double pmedianNormalizada = (calcularPmedian(instalaciones)-0)/(this.sumatorioDistanciasMaximas-0);
                        double pdispersionNormalizada = (calcularPdispersion(instalaciones) - this.distanciaMinimaGeneral)/(this.distanciaMaximaGeneral-this.distanciaMinimaGeneral);
                        double funcionAgregadaAux = alfa * -pmedianNormalizada + (1.0-alfa) * pdispersionNormalizada;
                        if(Double.compare(funcionAgregada, funcionAgregadaAux) < 0) {
                            funcionAgregada = funcionAgregadaAux;
                            nuevaInstalacion = j;
                        }
                        instalaciones[j] = false;
                    }
                }
                instalaciones[nuevaInstalacion] = true;
                conjuntoInstalaciones.add(nuevaInstalacion);
            }
            double pmedian = calcularPmedian(instalaciones.clone());
            double pdispersion = calcularPdispersion(instalaciones.clone());
            Solucion solucion = new Solucion(pmedian, pdispersion, instalaciones.clone());
            boolean metida = meterSolucion(solucion);
            if(metida){
                normalizarPmedian(solucion);
                normalizarPdispersion(solucion);
            }
            alfa += 0.001;
        }
        return new Date().getTime() - inicioEjecucion;
    }

    private double calcularPmedian(boolean[] instalaciones){
        double pmedian = 0;
        for(int i = 0; i < this.nodos; i++)
            if(!instalaciones[i]){
                double distanciaMinima = Double.MAX_VALUE;
                for(int j = 0; j < this.nodos; j++)
                    if(instalaciones[j])
                        distanciaMinima = Math.min(this.distancias[i][j], distanciaMinima);
                pmedian = pmedian + distanciaMinima;
            }
        return pmedian;
    }

    private double calcularPdispersion(boolean[] instalaciones){
        double pdispersion = Double.MAX_VALUE;
        for(int i = 0; i < this.nodos; i++)
            for(int j = i+1; j < this.nodos && instalaciones[i]; j++)
                if(instalaciones[j])
                    pdispersion = Math.min(pdispersion, this.distancias[i][j]);
        return pdispersion;
    }

    private double normalizarPmedian(Solucion solucion){
        double pmedian = solucion.getPmedian();
        double pmedianNormalizada = (pmedian-0)/(this.sumatorioDistanciasMaximas-0); //Bug
        solucion.setPmedianNormalizada(pmedianNormalizada);
        return pmedianNormalizada;
    }

    private double normalizarPdispersion(Solucion solucion){
        double pdispersion = solucion.getPdispersion();
        double pdispersionNormalizada = (pdispersion - this.distanciaMinimaGeneral)/(this.distanciaMaximaGeneral-this.distanciaMinimaGeneral);
        solucion.setPdispersionNormalizada(pdispersionNormalizada);
        return pdispersionNormalizada;
    }

    private boolean meterSolucion(Solucion solucion){
        this.solucionesBorradas.clear();
        double pmedian2 = solucion.getPmedian();
        double pdispersion2 = solucion.getPdispersion();
        for(Solucion s: this.soluciones){
            double pmedian1 = s.getPmedian();
            double pdispersion1 = s.getPdispersion();
            if(Double.compare(pmedian1, pmedian2) <= 0 && Double.compare(pdispersion1, pdispersion2) >= 0)
                return false;
            else if(Double.compare(pmedian1,pmedian2) >= 0 && Double.compare(pdispersion1,pdispersion2) <= 0)
                this.solucionesBorradas.add(s);
        }
        for(Solucion borrar: this.solucionesBorradas)
            this.soluciones.remove(borrar);
        this.soluciones.add(solucion);
        return true;
    }
    /*
    public long frentePareto(){
        long inicioEjecucion = new Date().getTime();
        for (boolean[] matrizCombinacionInstalaciones : this.matrizCombinacionInstalaciones) {
            double pmedian = calcularPmedian(matrizCombinacionInstalaciones);
            double pdispersion = calcularPdispersion(matrizCombinacionInstalaciones);
            Solucion solucion = new Solucion(pmedian, pdispersion, matrizCombinacionInstalaciones);
            boolean metida = meterSolucion(solucion);
            if(metida){
                normalizarPmedian(solucion);
                normalizarPdispersion(solucion);
            }
        }
        return new Date().getTime() - inicioEjecucion;
    }*/

    private void calcularMejorSolucion(){
        double valorMejorSolucion = Double.MIN_VALUE;
        for(Solucion s: this.soluciones){
            if(Double.compare(valorMejorSolucion, s.getPmedianNormalizada() + s.getPdispersionNormalizada()) < 0){
                this.mejor_solucion = s;
                valorMejorSolucion = s.getPmedianNormalizada() + s.getPdispersionNormalizada();
            }
        }
    }
    
    public long busquedaLocal(){
        calcularMejorSolucion();
        boolean frenteParetoMejorado = true;
        long inicioEjecucion = new Date().getTime();
        while(frenteParetoMejorado){
            frenteParetoMejorado = false;
            for(int i = 0; i < this.nodos && !frenteParetoMejorado; i++)
                for(int j = 0; j < this.nodos && !frenteParetoMejorado && this.mejor_solucion.getInstalaciones()[i]; j++)
                    if(!this.mejor_solucion.getInstalaciones()[j]){
                        boolean[] instalaciones = this.mejor_solucion.getInstalaciones().clone();
                        instalaciones[i] = false;
                        instalaciones[j] = true;
                        double pmedian = calcularPmedian(instalaciones);
                        double pdispersion = calcularPdispersion(instalaciones);
                        Solucion nuevaMejorSolucion = new Solucion(pmedian, pdispersion, instalaciones);
                        frenteParetoMejorado = meterSolucion(nuevaMejorSolucion);
                        if(frenteParetoMejorado) {
                            normalizarPmedian(nuevaMejorSolucion);
                            normalizarPdispersion(nuevaMejorSolucion);
                            this.mejor_solucion = nuevaMejorSolucion;
                        }
                    }
        }
        return new Date().getTime() - inicioEjecucion;
    }
}
