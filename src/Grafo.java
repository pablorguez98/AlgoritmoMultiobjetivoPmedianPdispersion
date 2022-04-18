import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Grafo {
    //Atributos de la clase Grafo.
    private final int numeroNodos, numeroInstalaciones;
    private double[][] distancias;
    private Nodo[] nodos;
    private double distanciaMinimaGeneral, distanciaMaximaGeneral, sumaDistanciasMinimas, sumaDistanciasMaximas;
    private ArrayList<Solucion> FPAleatorio = new ArrayList<>(), FPGreedy = new ArrayList<>(), solucionesBoradas = new ArrayList<>();

    //Constructor de la clase Grafo.
    public Grafo(int numeroNodos, int numeroInstalaciones){
        this.numeroNodos = numeroNodos;
        this.numeroInstalaciones = numeroInstalaciones;
        this.distancias = new double[numeroNodos][numeroNodos];
        this.nodos = new Nodo[numeroNodos];
        this.distanciaMinimaGeneral = Double.MAX_VALUE;
        this.distanciaMaximaGeneral = Double.MIN_VALUE;
        this.sumaDistanciasMinimas = 0;
        this.sumaDistanciasMaximas = 0;
        for(int i = 0; i < numeroNodos; i++){
            this.nodos[i] = new Nodo();
            for(int j = 0; j < numeroNodos; j++){
                if(i == j)
                    this.distancias[i][j] = 0;
                else
                    this.distancias[i][j] = Double.MAX_VALUE;
            }
        }
    }

    //Métodos get y set de la clase Grafo.
    //Método que devuelve el Frente de Pareto generado con instalaciones aleatorias.
    public ArrayList<Solucion> getFPAleatorio(){
        return this.FPAleatorio;
    }

    //Método que devuelve el Frente de pareto generado con instalaciones greedy.
    public ArrayList<Solucion> getFPGreedy(){
        return this.FPGreedy;
    }

    //Método que modifica la distancia entre dos nodos del grafo.
    public void modificarDistancia(int nodoA, int nodoB, double distancia){
        this.distancias[nodoA][nodoB] = Math.min(distancia, this.distancias[nodoA][nodoB]);
        this.distancias[nodoB][nodoA] = Math.min(distancia, this.distancias[nodoB][nodoA]);
    }

    //Método que aplica el algoritmo de Floyd-Warshall para el cálculo de las distancias restantes del grafo.
    public void calcularDistancias(){
        //PASO 1: Floyd-Warshall --> se busca si la distancia de A a B es más corta si paso antes por C.
        for(int i = 0; i < this.numeroNodos; i++)
            for(int j = 0; j < this.numeroNodos; j++)
                for(int k = 0; k < this.numeroNodos; k++)
                    this.distancias[i][j] = Math.min(this.distancias[i][j], (this.distancias[i][k]+this.distancias[k][j]));
        //PASO 2: Se mira cuál es la distancia mínima y máxima del grafo, tanto particular como general.
        for(int i = 0; i < this.numeroNodos; i++){
            for(int j = 0; j < this.numeroNodos; j++)
                if(i != j){
                    this.nodos[i].setDistanciaMinima(Math.min(this.nodos[i].getDistanciaMinima(), this.distancias[i][j]));
                    this.nodos[i].setDistanciaMaxima(Math.max(this.nodos[i].getDistanciaMaxima(), this.distancias[i][j]));
                    this.distanciaMinimaGeneral = Math.min(this.distanciaMinimaGeneral, this.nodos[i].getDistanciaMinima());
                    this.distanciaMaximaGeneral = Math.max(this.distanciaMaximaGeneral, this.nodos[i].getDistanciaMaxima());
                }
            this.sumaDistanciasMinimas += this.nodos[i].getDistanciaMinima();
            this.sumaDistanciasMaximas += this.nodos[i].getDistanciaMaxima();
        }
    }

    //Método que calcula el valor de la función objetivo pmedian.
    private double calcularPmedian(boolean[] instalaciones){
        double pmedian = 0;
        double distanciaMinima;
        for(int i = 0; i < this.numeroNodos; i++)
            if(!instalaciones[i]){
                distanciaMinima = Double.MAX_VALUE;
                for(int j = 0; j < this.numeroNodos; j++)
                    if(instalaciones[j])
                        distanciaMinima = Math.min(distanciaMinima, this.distancias[i][j]);
                pmedian += distanciaMinima;
            }
        return pmedian;
    }

    //Método que calcula el valor de la función objetivo pdispersion.
    private double calcularPdispersion(boolean[] instalaciones){
        double pdispersion = Double.MAX_VALUE;
        for(int i = 0; i < this.numeroNodos; i++)
            for(int j = i+1; j < this.numeroNodos && instalaciones[i]; j++)
                if(instalaciones[j])
                    pdispersion = Math.min(pdispersion, this.distancias[i][j]);
        return pdispersion;
    }

    //Método que normaliza el valor de la función objetivo pmedian.
    private double normalizarPmedian(double pmedian){
        //return (pmedian - this.sumaDistanciasMinimas)/(this.sumaDistanciasMaximas-this.sumaDistanciasMinimas);
        return (pmedian - 0)/(this.sumaDistanciasMaximas - 0);
    }

    //Método que normaliza el valor de la función objetivo pdispersion.
    private double normalizarPdispersion(double pdispersion){
        return (pdispersion - this.distanciaMinimaGeneral)/(this.distanciaMaximaGeneral - this.distanciaMinimaGeneral);
    }

    //Método que evalúa si una solución debe ser introducida o no.
    private boolean meterSolucion(Solucion solucion, ArrayList<Solucion> soluciones){
        this.solucionesBoradas.clear();
        double pmedianSolucion = solucion.getPmedian(), pdispersionSolucion = solucion.getPdispersion(), pmedianS, pdispersionS;
        for(Solucion s: soluciones){
            pmedianS = s.getPmedian();
            pdispersionS = s.getPdispersion();
            if(Double.compare(pmedianS, pmedianSolucion) <= 0 && Double.compare(pdispersionS, pdispersionSolucion) >= 0)
                return false;
            else if(Double.compare(pmedianS, pmedianSolucion) >= 0 && Double.compare(pdispersionS, pdispersionSolucion) <= 0)
                this.solucionesBoradas.add(s);
        }
        for(Solucion solucionBorrar: this.solucionesBoradas)
            soluciones.remove(solucionBorrar);
        soluciones.add(solucion);
        return true;
    }

    //Método que genera un Frente de Pareto con instalaciones aleatorias.
    public long FrenteParetoAleatorio(){
        double alfa = 0.0;
        Random random = new Random(13);
        ArrayList<Integer> conjuntoInstalaciones = new ArrayList<>();
        boolean[] instalaciones = new boolean[this.numeroNodos];
        int instalacionPartida;
        long inicioEjecucion = new Date().getTime();
        while(alfa <= 1.0){
            for (Integer n : conjuntoInstalaciones)
                instalaciones[n] = false;
            conjuntoInstalaciones.clear();
            instalacionPartida = random.nextInt(this.numeroNodos);
            instalaciones[instalacionPartida] = true;
            conjuntoInstalaciones.add(instalacionPartida);
            for(int i = 1; i < this.numeroInstalaciones; i++){
                int nuevaInstalacion = random.nextInt(this.numeroNodos);
                if(!instalaciones[nuevaInstalacion]){
                    instalaciones[nuevaInstalacion] = true;
                    conjuntoInstalaciones.add(nuevaInstalacion);
                }else
                    i--;
            }
            double pmedian = calcularPmedian(instalaciones);
            double pdispersion = calcularPdispersion(instalaciones);
            double pmedianNormalizada = normalizarPmedian(pmedian);
            double pdispersionNormalizada = normalizarPdispersion(pdispersion);
            Solucion solucion = new Solucion(pmedian, pdispersion, pmedianNormalizada, pdispersionNormalizada, instalaciones);
            meterSolucion(solucion, this.FPAleatorio);
            alfa += 0.01;
        }
        return new Date().getTime() - inicioEjecucion;
    }

    //Método que genera un Frente de Pareto con instalaciones greedy.
    public long FrenteParetoGreedy(){
        double alfa = 0.0, funcionAgregada, pmedian = Double.MAX_VALUE, pmedianNormalizada = Double.MAX_VALUE, pdispersion = Double.MIN_VALUE, pdispersionNormalizada = Double.MIN_VALUE;
        Random random = new Random(13);
        ArrayList<Integer> conjuntoInstalaciones = new ArrayList<>();
        boolean[] instalaciones = new boolean[this.numeroNodos];
        int instalacionPartida;
        long inicioEjecucion = new Date().getTime();
        while (alfa <= 1.0) {
            for (Integer n : conjuntoInstalaciones)
                instalaciones[n] = false;
            conjuntoInstalaciones.clear();
            instalacionPartida = random.nextInt(this.numeroNodos);
            conjuntoInstalaciones.add(instalacionPartida);
            instalaciones[instalacionPartida] = true;
            for(int i = 1; i < this.numeroInstalaciones; i++){
                funcionAgregada = Double.MIN_VALUE;
                int nuevaInstalacion = random.nextInt(this.numeroNodos); //Valor de inicio basura.
                for(int j = 0; j < this.numeroNodos; j++)
                    if(!instalaciones[j]){
                        instalaciones[j] = true;
                        pmedian = calcularPmedian(instalaciones);
                        pmedianNormalizada = normalizarPmedian(pmedian);
                        pdispersion = calcularPdispersion(instalaciones);
                        pdispersionNormalizada = normalizarPdispersion(pdispersion);
                        double funcionAgregadaAux = alfa * (-pmedianNormalizada) + (1.0 - alfa) * pdispersionNormalizada;
                        if (Double.compare(funcionAgregada, funcionAgregada) < 0) {
                            funcionAgregada = funcionAgregadaAux;
                            nuevaInstalacion = j;
                        }
                        instalaciones[j] = false;
                    }
                instalaciones[nuevaInstalacion] = true;
                conjuntoInstalaciones.add(nuevaInstalacion);
            }
            Solucion solucion = new Solucion(pmedian, pdispersion, pmedianNormalizada, pdispersionNormalizada, instalaciones);
            meterSolucion(solucion, this.FPGreedy);
            alfa += 0.01;
        }
        return new Date().getTime() - inicioEjecucion;
    }

    //Método que evalúa cuál es la mejor solución de un Frente de Pareto.
    private Solucion calcularMejorSolucion(ArrayList<Solucion> soluciones){
        Solucion mejorSolucion = null;
        double valorMejorSolucion = Double.MIN_VALUE;
        for(Solucion s: soluciones)
            if(Double.compare(valorMejorSolucion, s.getPmedianNormalizado() + s.getPdispersionNormalizado()) < 0){
                mejorSolucion = s;
                valorMejorSolucion = s.getPmedianNormalizado() + s.getPdispersionNormalizado();
            }
        return mejorSolucion;
    }

    //Método que genera un Frente de Pareto aplicando una búsqueda local con instalaciones aleatorias o greedy.
    public long BusquedaLocalGeneral(int modo){
        Solucion mejorSolucion = null;
        if(modo == 0)
            mejorSolucion = calcularMejorSolucion(this.FPAleatorio);
        else if(modo == 1)
            mejorSolucion = calcularMejorSolucion(this.FPGreedy);
        boolean frenteParetoMejorado = true;
        boolean[] instalaciones;
        double pmedian, pmedianNormalizada, pdispersion, pdispersionNormalizada;
        long inicioEjecucion = new Date().getTime();
        while (frenteParetoMejorado) {
            frenteParetoMejorado = false;
            for (int i = 0; i < this.numeroNodos && !frenteParetoMejorado; i++)
                for (int j = 0; j < this.numeroNodos && !frenteParetoMejorado && mejorSolucion.getInstalaciones()[i]; j++)
                    if (!mejorSolucion.getInstalaciones()[j]) {
                        instalaciones = mejorSolucion.getInstalaciones().clone();
                        instalaciones[i] = false;
                        instalaciones[j] = true;
                        pmedian = calcularPmedian(instalaciones);
                        pmedianNormalizada = normalizarPmedian(pmedian);
                        pdispersion = calcularPdispersion(instalaciones);
                        pdispersionNormalizada = normalizarPdispersion(pdispersion);
                        Solucion nuevaMejorSolucion = new Solucion(pmedian, pdispersion, pmedianNormalizada, pdispersionNormalizada, instalaciones);
                        if (modo == 0)
                            frenteParetoMejorado = meterSolucion(nuevaMejorSolucion, this.FPAleatorio);
                        else if (modo == 1)
                            frenteParetoMejorado = meterSolucion(nuevaMejorSolucion, this.FPGreedy);
                        if (frenteParetoMejorado)
                            mejorSolucion = nuevaMejorSolucion;
                    }
        }
        return new Date().getTime() - inicioEjecucion;
    }
}
