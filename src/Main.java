import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.io.*;
import java.util.ArrayList;

public class Main {
    //Atributos
    private static Grafo mapa = null; //Representa el mapa sobre el que se va a trabajar.
    private static final String ruta = "C:/Users/pablo/OneDrive/Escritorio/TFG/";

    public static void main(String[] args) throws IOException {
        /* PASO 1.
               1. Se accede a la carpeta que contiene los mapas y se lista cada uno de ellos.
         */
        File carpeta = new File(ruta+"Instancias");
        String[] grafos = carpeta.list();
        if(grafos != null){
            for(String grafo: grafos){
                /* PASO 2. Para cada mapa:
                       1. Se obtiene el nombre del mapa y se realiza una lectura de la informacion del mapa.
                       2. Se inicializa el mapa con la información leída como el número de nodos o de instalaciones,
                          así como con las distancias entre cada punto.
                 */
                System.out.println("Grafo: " + grafo + ":");
                FileReader lectura = new FileReader(ruta+"Instancias/" + grafo);
                BufferedReader transmision = new BufferedReader(lectura);
                String linea;
                int linea_leida = 1;
                while((linea= transmision.readLine()) != null){
                    String[] informacion1 = linea.split(" ");
                    String[] informacion = new String[informacion1.length-1];
                    System.arraycopy(informacion1, 1, informacion, 0, informacion.length);
                    if(linea_leida == 1){
                        int nodos = Integer.parseInt(informacion[0]);
                        int instalaciones = Integer.parseInt(informacion[2]);
                        mapa = new Grafo(nodos, instalaciones);
                    }else{
                        int nodoA = Integer.parseInt(informacion[0])-1;
                        int nodoB = Integer.parseInt(informacion[1])-1;
                        double distanciaAB = Integer.parseInt(informacion[2]);
                        mapa.meterDistancia(nodoA, nodoB, distanciaAB);
                    }
                    linea_leida++;
                }
                transmision.close();
                lectura.close();
                /* Paso 3. Una vez inicializado el grafo:
                       1. Se evalúan el resto de distancias para obtener las distancias de un nodo a cualquier otro
                          mediante el Algoritmo de Floyd-Warshall.
                       2. Se establace unos conjuntos de instalaciones de partida para la evaluación del algoritmo.
                       3. Se genera un Frente de Pareto con un conjunto de X soluciones, todas igual de válidas,
                          así como se establece cuál es la mejor solución de ese conjunto de X soluciones.
                       4. A partir de la mejor solución, se aplica una Búsqueda Local con el fin de mejorar el Frente
                          de Pareto.
                 */
                if(mapa != null){
                    mapa.calcularDistancias();
                    long ejecucionFP = mapa.frentePareto()/1000;
                    ArrayList<Solucion> solucionesFP = (ArrayList<Solucion>) mapa.getSoluciones().clone();
                    int numeroFP = solucionesFP.size();
                    long ejecucionBL = mapa.busquedaLocal() /1000;
                    ArrayList<Solucion> solucionesBL = (ArrayList<Solucion>) mapa.getSoluciones().clone();
                    int numeroBL = solucionesBL.size();
                    /* Paso 4.
                           1. Se generan unos ficheros .png con la representación del Frente de Pareto y de la
                              Búsqueda Local.
                           2. Se generan unos ficheros .txt con la información del Frente de Pareto y la Búsqueda Local.
                     */
                    final XYSeries frentePareto = new XYSeries("Frente de Pareto");
                    final XYSeries busquedaLocal = new XYSeries("Busqueda local");
                    for (Solucion value : solucionesFP)
                        frentePareto.add(value.getPmedian(), value.getPdispersion());
                    for (Solucion solucion : solucionesBL)
                        busquedaLocal.add(solucion.getPmedian(), solucion.getPdispersion());
                    final XYSeriesCollection series = new XYSeriesCollection();
                    series.addSeries(frentePareto);
                    series.addSeries(busquedaLocal);
                    try{
                        final JFreeChart grafica = new Grafica().crear_grafica(series, "Grafo: " + grafo);
                        ChartUtilities.saveChartAsPNG(new File(ruta+"Graficas/"+grafo+".png"), grafica, 400, 300);
                        File fichero = new File(ruta+"Frentes/"+grafo);
                        FileWriter escritura = new FileWriter(fichero);
                        BufferedWriter comunicacion = new BufferedWriter(escritura);
                        comunicacion.write("Frente de Pareto:\n");
                        comunicacion.write("\tNumero de soluciones: " + numeroFP +"\n");
                        comunicacion.write("\tTiempo de ejecucion (sg): " + ejecucionFP +"\n");
                        for(Solucion s: solucionesFP)
                            comunicacion.write("\t\tSolucion -> pmedian: "+s.getPmedian()+" pdispersion: "+s.getPdispersion()+"\n");
                        comunicacion.write("Busqueda local:\n");
                        comunicacion.write("\tNumero de soluciones: " + numeroBL+"\n");
                        comunicacion.write("\tTiempo de ejecucion (sg): " + ejecucionBL +"\n");
                        for(Solucion s: solucionesBL)
                            comunicacion.write("\t\tSolucion -> pmedian: "+s.getPmedian()+" pdispersion: "+s.getPdispersion()+"\n");
                        comunicacion.close();
                        escritura.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
