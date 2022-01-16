import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main (String[] args) throws IOException {
        File carpeta =  new File("C:/Users/pablo/OneDrive/Escritorio/Algoritmo pmedian pdispersion/Instancias");
        String[] grafos = carpeta.list();
        assert grafos != null;
        for(String nombre_grafo: grafos){
            System.out.println("Grafo "+nombre_grafo+":");
            FileReader lectura = new FileReader("C:/Users/pablo/OneDrive/Escritorio/Algoritmo pmedian pdispersion/Instancias/"+nombre_grafo);
            BufferedReader transmision = new BufferedReader(lectura);
            String linea_leida;
            int i = 1;
            Grafo grafo = null;
            while((linea_leida=transmision.readLine()) != null){
                String[] informacion1 = linea_leida.split(" ");
                String[] informacion2 = new String[informacion1.length-1];
                System.arraycopy(informacion1,1,informacion2,0,informacion2.length);
                if(i==1){
                    int numero_nodos = Integer.parseInt(informacion2[0]);
                    int numero_instalaciones = Integer.parseInt(informacion2[2]);
                    grafo = new Grafo(numero_nodos, numero_instalaciones);
                }else{
                    int nodo1 = Integer.parseInt(informacion2[0]);
                    int nodo2 = Integer.parseInt(informacion2[1]);
                    double distancia = Integer.parseInt(informacion2[2]);
                    grafo.getDistancias()[nodo1-1][nodo2-1]=distancia;
                    grafo.getDistancias()[nodo2-1][nodo1-1]=distancia;
                }
                i++;
            }
            transmision.close();
            lectura.close();
            assert grafo != null;
            grafo.calcular_distancias();
            grafo.calcular_instalaciones();
            long ejecucion_frente_pareto = grafo.frente_pareto();
            ArrayList<Solucion> frente_pareto1 = (ArrayList<Solucion>) grafo.getSoluciones().clone();
            System.out.println("\tFrente de Pareto:");
            System.out.println("\t\tNumero de soluciones: " + grafo.getSoluciones().size());
            System.out.println("\t\tTiempo de ejecucuion (sg): " + ejecucion_frente_pareto/1000);
            long ejecucion_busqueda_local = grafo.busqueda_local();
            ArrayList<Solucion> busqueda_local1 = (ArrayList<Solucion>) grafo.getSoluciones().clone();
            System.out.println("\tBusqueda local:");
            System.out.println("\t\tNumero de soluciones: " + grafo.getSoluciones().size());
            System.out.println("\t\tTiempo de ejecucuion (sg): " + ejecucion_busqueda_local/1000);

            final XYSeries frente_pareto2 = new XYSeries("Frente de Pareto");
            final XYSeries busqueda_local2 = new XYSeries("Busqueda local");
            for (Solucion value : frente_pareto1)
                frente_pareto2.add(value.getPmedian(), value.getPdispersion());
            for (Solucion solucion : busqueda_local1)
                busqueda_local2.add(solucion.getPmedian(), solucion.getPdispersion());
            final XYSeriesCollection collection = new XYSeriesCollection();
            collection.addSeries(frente_pareto2);
            collection.addSeries(busqueda_local2);
            try{
                final JFreeChart grafica_XY = new Grafica().crear_grafica(collection, nombre_grafo);
                ChartUtilities.saveChartAsPNG(new File(nombre_grafo+".png"), grafica_XY, 400, 300);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}