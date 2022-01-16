import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

public class Grafo {

    //Atributos de la clase Grafo
    private int numero_nodos;
    private int numero_instalaciones;
    private double[][] distancias;
    private double distancia_minima_general;
    private double distancia_maxima_general;
    private double distancias_minimas_totales;
    private double distancias_maximas_totales;
    private Nodo[] nodos;
    private ArrayList<ArrayList<Integer>> instalaciones = new ArrayList<>();
    private ArrayList<Solucion> soluciones = new ArrayList<>();
    private Solucion mejor_solucion;

    //Constructor de la clase Grafo
    public Grafo(int numero_nodos, int numero_instalaciones){
        this.numero_nodos=numero_nodos;
        this.numero_instalaciones=numero_instalaciones;
        this.nodos = new Nodo[numero_nodos];
        this.distancias_minimas_totales = 0;
        this.distancias_maximas_totales = 0;
        this.distancia_maxima_general = Double.MIN_VALUE;
        this.distancia_minima_general = Double.MAX_VALUE;
        this.mejor_solucion = null;
        this.distancias = new double[numero_nodos][numero_nodos];
        for(int i = 0; i < numero_nodos; i++) {
            this.nodos[i] = new Nodo(i + 1);
            for(int j = 0; j < numero_nodos; j++)
                this.distancias[i][j] = Double.MAX_VALUE;
        }
    }

    //Metodos get y set de la clase Grafo
    public int getNumero_nodos(){
        return this.numero_nodos;
    }

    public int getNumero_instalaciones(){
        return this.numero_instalaciones;
    }

    public double[][] getDistancias() {
        return this.distancias;
    }

    public double getDistancia_maxima_general() {
        return this.distancia_maxima_general;
    }

    public double getDistancia_minima_general() {
        return this.distancia_minima_general;
    }

    public double getDistancias_maximas_totales() {
        return this.distancias_maximas_totales;
    }

    public double getDistancias_minimas_totales() {
        return this.distancias_minimas_totales;
    }

    public Nodo[] getNodos(){
        return this.nodos;
    }

    public ArrayList<ArrayList<Integer>> getInstalaciones() {
        return this.instalaciones;
    }

    public ArrayList<Solucion> getSoluciones(){
        return this.soluciones;
    }

    public Solucion getMejor_solucion(){
        return this.mejor_solucion;
    }

    public void setDistancia_maxima_general(double distancia_maxima_general) {
        this.distancia_maxima_general = distancia_maxima_general;
    }

    public void setDistancia_minima_general(double distancia_minima_general) {
        this.distancia_minima_general = distancia_minima_general;
    }

    public void setDistancias_minimas_totales(double distancias_minimas_totales) {
        this.distancias_minimas_totales = distancias_minimas_totales;
    }

    public void setDistancias_maximas_totales(double distancias_maximas_totales) {
        this.distancias_maximas_totales = distancias_maximas_totales;
    }

    public void setMejor_solucion(Solucion mejor_solucion) {
        this.mejor_solucion = mejor_solucion;
    }

    //Metodos principales de la clase Grafo
    public void calcular_distancias(){
        for(int i = 0; i < getNumero_nodos();i++)
            for(int j = 0; j < getNumero_nodos(); j++)
                for(int k = 0; k < getNumero_nodos(); k++)
                    getDistancias()[i][j] = Math.min(getDistancias()[i][j], getDistancias()[i][k]+getDistancias()[k][j]);
    }

    private void calcular_maximos_minimos(ArrayList<Integer> combinacion_instalaciones){
        HashSet<Integer> copia_combinacion_instalaciones = new HashSet<>(combinacion_instalaciones);
        setDistancia_minima_general(Double.MAX_VALUE);
        setDistancia_maxima_general(Double.MIN_VALUE);
        setDistancias_minimas_totales(0);
        setDistancias_maximas_totales(0);
        for(int i = 0; i < getNumero_nodos(); i++)
            if(!copia_combinacion_instalaciones.contains(i)){
                for(int j = 0; j < getNumero_nodos(); j++)
                    if(i!=j){
                        if(Double.compare(getDistancia_minima_general(), getDistancias()[i][j]) > 0)
                            setDistancia_minima_general(getDistancias()[i][j]);
                        if(Double.compare(getDistancia_maxima_general(), getDistancias()[i][j]) < 0)
                            setDistancia_maxima_general(getDistancias()[i][j]);
                        if(Double.compare(getNodos()[i].getDistancia_minima(), getDistancias()[i][j]) > 0)
                            getNodos()[i].setDistancia_minima(getDistancias()[i][j]);
                        if(Double.compare(getNodos()[i].getDistancia_maxima(), getDistancias()[i][j]) < 0)
                            getNodos()[i].setDistancia_maxima(getDistancias()[i][j]);
                    }
                setDistancias_minimas_totales(getDistancias_minimas_totales()+getNodos()[i].getDistancia_minima());
                setDistancias_maximas_totales(getDistancias_maximas_totales()+getNodos()[i].getDistancia_maxima());
            }
    }

    public void calcular_instalaciones(){
        Random random = new Random(13);
        for(int i = 0; i < 5000; i++){
            boolean[] metido = new boolean[getNumero_nodos()];
            ArrayList<Integer> instalaciones = new ArrayList<>();
            for(int j = 0; j < getNumero_instalaciones(); j++){
                int nodo_instalacion = random.nextInt(getNumero_nodos());
                if(!metido[nodo_instalacion]){
                    metido[nodo_instalacion] = true;
                    instalaciones.add(nodo_instalacion);
                }else
                    j--;
            }
            getInstalaciones().add(instalaciones);
        }
    }

    private double calcular_pmedian(ArrayList<Integer> combinacion_instalaciones){
        double pmedian = 0;
        HashSet<Integer> copia_combinacion_instalaciones = new HashSet<>(combinacion_instalaciones);
        for(int i = 0; i < getNumero_nodos(); i++)
            if(!copia_combinacion_instalaciones.contains(i)){
                double distancia_minima = Double.MAX_VALUE;
                for(int j = 0; j < getNumero_instalaciones(); j++)
                    if(Double.compare(getDistancias()[i][combinacion_instalaciones.get(j)], distancia_minima) < 0)
                        distancia_minima = getDistancias()[i][combinacion_instalaciones.get(j)];
                pmedian = pmedian + distancia_minima;
            }
        return pmedian;
    }

    private double calcular_pdispersion(ArrayList<Integer> combinacion_instalaciones){
        double pdispersion = Double.MAX_VALUE;
        for(int i = 0; i < getNumero_instalaciones(); i++)
            for(int j = i+1; j < getNumero_instalaciones(); j++)
                if(getDistancias()[combinacion_instalaciones.get(i)][combinacion_instalaciones.get(j)] < pdispersion)
                    pdispersion = getDistancias()[combinacion_instalaciones.get(i)][combinacion_instalaciones.get(j)];
        return pdispersion;
    }

    private void normalizar(Solucion solucion){
        double pmedian = solucion.getPmedian();
        double pdispersion = solucion.getPdispersion();
        solucion.setPmedian((pmedian-getDistancias_minimas_totales())/(getDistancias_maximas_totales()-getDistancias_minimas_totales()));
        solucion.setPdispersion((pdispersion-getDistancia_minima_general())/(getDistancia_maxima_general()-getDistancia_minima_general()));
    }

    private boolean meter_solucion(Solucion solucion1){
        if(getSoluciones().isEmpty()){
            getSoluciones().add(solucion1);
            return true;
        }else{
            ArrayList<Solucion> soluciones_borradas = new ArrayList<>();
            double pmedian = solucion1.getPmedian();
            double pdispersion = solucion1.getPdispersion();
            int i = 0;
            while(i < getSoluciones().size()){
                Solucion solucion2 = getSoluciones().get(i);
                if(solucion2.getPmedian() <= pmedian && solucion2.getPdispersion() >= pdispersion)
                    return false;
                else if(solucion2.getPmedian()>= pmedian && solucion2.getPdispersion() <= pdispersion){
                    i++;
                    soluciones_borradas.add(solucion2);
                }else
                    i++;
            }
            for(Solucion solucion_borrada: soluciones_borradas)
                getSoluciones().remove(solucion_borrada);
            getSoluciones().add(solucion1);
            return  true;
        }
    }

    public long frente_pareto(){
        Solucion solucion;
        long tiempo_ejecucion = new Date().getTime();
        for(ArrayList<Integer> combinacion_instalaciones: getInstalaciones()){
            calcular_maximos_minimos(combinacion_instalaciones);
            double pmedian = calcular_pmedian(combinacion_instalaciones);
            double pdispersion = calcular_pdispersion(combinacion_instalaciones);
            solucion = new Solucion(pmedian, pdispersion, combinacion_instalaciones);
            normalizar(solucion);
            boolean metida = meter_solucion(solucion);
            if(metida && ((getMejor_solucion() == null) || ((getMejor_solucion().getPmedian()+getMejor_solucion().getPdispersion()) < solucion.getPmedian()+solucion.getPdispersion())))
                setMejor_solucion(solucion);
        }
        return new Date().getTime()-tiempo_ejecucion;
    }

    public long busqueda_local(){
        boolean[] es_instalacion = new boolean[getNumero_nodos()];
        boolean frente_pareto_mejorado = true;
        long tiempo_ejecucion = new Date().getTime();
        while(frente_pareto_mejorado){
            frente_pareto_mejorado = false;
            for(Integer i: getMejor_solucion().getCombinacion_instalaciones())
                es_instalacion[i] = true;
            for(int i = 0; i < getNumero_instalaciones() && !frente_pareto_mejorado; i++){
                for(int j = 0; j < getNumero_nodos() && !frente_pareto_mejorado; j++){
                    if (!es_instalacion[j]){
                        ArrayList<Integer> nuevas_instalaciones = new ArrayList<>(getMejor_solucion().getCombinacion_instalaciones());
                        nuevas_instalaciones.remove(i);
                        nuevas_instalaciones.add(j);
                        calcular_maximos_minimos(nuevas_instalaciones);
                        double nuevo_pmedian = calcular_pmedian(nuevas_instalaciones);
                        double nuevo_pdispersion = calcular_pdispersion(nuevas_instalaciones);
                        Solucion nueva_mejor_solucion = new Solucion(nuevo_pmedian, nuevo_pdispersion, nuevas_instalaciones);
                        normalizar(nueva_mejor_solucion);
                        frente_pareto_mejorado = meter_solucion(nueva_mejor_solucion);
                        if(frente_pareto_mejorado){
                            setMejor_solucion(nueva_mejor_solucion);
                            es_instalacion[getMejor_solucion().getCombinacion_instalaciones().get(i)] = false;
                            es_instalacion[j] = true;
                        }else
                            calcular_maximos_minimos(getMejor_solucion().getCombinacion_instalaciones());
                    }
                }
            }
        }
        return new Date().getTime() - tiempo_ejecucion;
    }
}
