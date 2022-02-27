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
    private boolean [][] matrizCombinacionInstalaciones;
    private final int numeroCombinaciones = 5000;

    //Constructor
    public Grafo(int nodos, int instalaciones){
        this.nodos = nodos;
        this.instalaciones = instalaciones;
        this.distancias = new double[nodos][nodos];
        this.puntosInteres = new Nodo[nodos];
        this.matrizCombinacionInstalaciones = new boolean [this.numeroCombinaciones][nodos];
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
        for(int i = 0; i < this.numeroCombinaciones; i++)
            for (int j = 0; j < this.instalaciones; j++) {
                int nodoInstalacion = random.nextInt(this.nodos);
                if (!this.matrizCombinacionInstalaciones[i][nodoInstalacion])
                    this.matrizCombinacionInstalaciones[i][nodoInstalacion] = true;
                else
                    j--;
            }
    }

    private void calcularDistanciasNodos(boolean[] combinacionInstalaciones){
        this.distanciaMinimaGeneral = Double.MAX_VALUE;
        this.distanciaMaximaGeneral = Double.MIN_VALUE;
        this.sumatorioDistanciasMinimas = 0;
        this.sumatorioDistanciasMaximas = 0;
        /*
            Paso 1. Para cada nodo i y cada nodo j que no es una instalación y que i es distinto de j:
                1. Se evalúa si la distancia mínima general es mayor que la distancia del nodo i al j. Si la distancia
                   de i a j es menor que la mínima general, esta última se actualiza con la distancia de i a j.
                2. Se evalúa si la distancia máxima general es menor que la distancia del nodo i al j. Si la distancia
                   de i a j es mayor que la máxima general, esta última se actualiza con la distancia de i a j.
                3. Se evalúa si la distancia mínima del nodo i es mayor que la distancia del nodo i al j. Si la
                   distancia de i a j es menor que la mínima del nodo i, esta última se actualiza con la distancia de i
                   a j.
                4. Se evalúa si la distancia máxima del nodo i es menor que la distancia del nodo i al nodo j. Si la
                   distancia de i a j es mayor que la máxima del nodo i, esta última se actualiza con la distancia de i
                   a j.
                5. Se suma al sumatorio de las distancias mínimas del grafo el valor de la distancia mínima del nodo i.
                6. Se suma al sumatorio de las distancias mínimas del grafo el valor de la distancia mínima del nodo i.
         */
        for(int i = 0; i < this.nodos; i ++)
            if(!combinacionInstalaciones[i]){
                for(int j = 0; j < this.nodos; j++)
                    if(i != j && !combinacionInstalaciones[j]){
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
        double pmedianNormalizada = (pmedian-this.sumatorioDistanciasMinimas)/(this.sumatorioDistanciasMaximas-this.sumatorioDistanciasMinimas);
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
        double pmedian2 = normalizarPmedian(solucion);
        double pdispersion2 = normalizarPdispersion(solucion);
        for(Solucion s: this.soluciones){
            double pmedian1 = s.getPmedianNormalizada();
            double pdispersion1 = s.getPdispersionNormalizada();
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

    public long frentePareto(){
        long inicioEjecucion = new Date().getTime();
        for (boolean[] matrizCombinacionInstalaciones : this.matrizCombinacionInstalaciones) {
            calcularDistanciasNodos(matrizCombinacionInstalaciones);
            double pmedian = calcularPmedian(matrizCombinacionInstalaciones);
            double pdispersion = calcularPdispersion(matrizCombinacionInstalaciones);
            Solucion solucion = new Solucion(pmedian, pdispersion, matrizCombinacionInstalaciones);
            meterSolucion(solucion);
        }
        return new Date().getTime() - inicioEjecucion;
    }

    private void calcularMejorSolucion(){
        double valorMejorSolucion = Double.MIN_VALUE;
        for(Solucion s: this.soluciones){
            double valorS = s.getPmedianNormalizada() + s.getPdispersionNormalizada();
            if(Double.compare(valorMejorSolucion, valorS) < 0){
                this.mejor_solucion = s;
                valorMejorSolucion = valorS;
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
                        calcularDistanciasNodos(instalaciones);
                        double pmedian = calcularPmedian(instalaciones);
                        double pdispersion = calcularPdispersion(instalaciones);
                        Solucion nuevaMejorSolucion = new Solucion(pmedian, pdispersion, instalaciones);
                        frenteParetoMejorado = meterSolucion(nuevaMejorSolucion);
                        if(frenteParetoMejorado)
                            calcularMejorSolucion();
                    }
        }
        return new Date().getTime() - inicioEjecucion;
    }
}
