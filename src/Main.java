import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.*;
import java.util.ArrayList;

public class Main {

    //Atributos
    private static Grafo mapa = null;

    public static void main(String[] args) throws IOException {
        File carpeta = new File("C:/Users/pablo/OneDrive/Escritorio/AlgoritmoMultiobjetivoPmedianPdispersion/Instancias");
        String[] grafos = carpeta.list();
        if(grafos != null){
            for(String grafo: grafos){
                System.out.println("Grafo: " + grafo + ":");
                FileReader lectura = new FileReader("C:/Users/pablo/OneDrive/Escritorio/AlgoritmoMultiobjetivoPmedianPdispersion/Instancias/" + grafo);
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
                if(mapa != null){
                    mapa.calcularDistancias();
                    mapa.calcularInstalaciones();
                    long ejecucionFrentePareto = mapa.frentePareto();
                    ArrayList<Solucion> solucionesFrentePareto = (ArrayList<Solucion>) mapa.getSoluciones().clone();
                    System.out.println("\tFrente de Pareto:");
                    System.out.println("\t\tNumero de soluciones: " + solucionesFrentePareto.size());
                    System.out.println("\t\tTiempo de ejecucion (sg): " + ejecucionFrentePareto/1000);
                    long ejecucionBusquedaLocal = mapa.busquedaLocal();
                    ArrayList<Solucion> solucionesBusquedaLocal = (ArrayList<Solucion>) mapa.getSoluciones().clone();
                    System.out.println("\tBusqueda local:");
                    System.out.println("\t\tNumero de soluciones: " + solucionesBusquedaLocal.size());
                    System.out.println("\t\tTiempo de ejecucion (sg): " + ejecucionBusquedaLocal/1000);
                    try{
                        String ruta = "C:/Users/pablo/OneDrive/Escritorio/AlgoritmoMultiobjetivoPmedianPdispersion/Frentes/"+grafo;
                        File file = new File(ruta);
                        if(!file.exists())
                            file.createNewFile();
                        FileWriter fw = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write("Frente de Pareto:\n");
                        bw.write("\tNumero de soluciones: " + solucionesFrentePareto.size()+"\n");
                        bw.write("\tTiempo de ejecucion (sg): " + ejecucionFrentePareto/1000+"\n");
                        int i = 1;
                        for(Solucion s: solucionesFrentePareto){
                            bw.write("\t\tSolucion "+i+"--> pmedian: "+s.getPmedian()+" pdispersion: "+s.getPdispersion()+"\n");
                            i++;
                        }
                        bw.write("Busqueda localo:\n");
                        bw.write("\tNumero de soluciones: " + solucionesBusquedaLocal.size()+"\n");
                        bw.write("\tTiempo de ejecucion (sg): " + ejecucionBusquedaLocal/1000+"\n");
                        i = 1;
                        for(Solucion s: solucionesBusquedaLocal){
                            bw.write("\t\tSolucion "+i+"--> pmedian: "+s.getPmedian()+" pdispersion: "+s.getPdispersion()+"\n");
                            i++;
                        }
                        bw.close();
                        fw.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    final XYSeries frentePareto = new XYSeries("Frente de Pareto");
                    final XYSeries busquedaLocal = new XYSeries("Busqueda local");
                    for (Solucion value : solucionesFrentePareto)
                        frentePareto.add(value.getPmedian(), value.getPdispersion());
                    for (Solucion solucion : solucionesBusquedaLocal)
                        busquedaLocal.add(solucion.getPmedian(), solucion.getPdispersion());
                    final XYSeriesCollection collection = new XYSeriesCollection();
                    collection.addSeries(frentePareto);
                    collection.addSeries(busquedaLocal);
                    try{
                        final JFreeChart grafica_XY = new Grafica().crear_grafica(collection, grafo, solucionesFrentePareto.size(), ejecucionFrentePareto/1000, solucionesBusquedaLocal.size(),ejecucionBusquedaLocal/1000);
                        ChartUtilities.saveChartAsPNG(new File("C:/Users/pablo/OneDrive/Escritorio/AlgoritmoMultiobjetivoPmedianPdispersion/Graficas/"+grafo+".png"), grafica_XY, 400, 300);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
