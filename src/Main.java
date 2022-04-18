import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.io.*;
import java.util.ArrayList;

public class Main {
    //Atributos de la clase Main.
    private static final String fuenteDatos = "C:/Users/pablo/OneDrive/Escritorio/AlgoritmoMultiobjetivo";
    private static Grafo grafo = null;

    //Método que general el conjunto de posibles soluciones para cada grafo de la fuente de datos.
    public static void main(String[] args) throws IOException {
        int nodoA, nodoB, numeroSolucionesFPAleatorio, numeroSolucionesFPGreedy, numeroSolucionesBLAleatorio, numeroSolucionesBLGreedy, numeroNodos, numeroInstalaciones, lineaLeida;
        double distnaciaAB;
        String[] informacion1, informacion2;
        ArrayList<Solucion> solucionesFPAleatorio, solucionesFPGreedy, solucionesBLAleatorio, solucionesBLGreedy;
        long tiempoEjecucionFPAleatorio, tiempoEjecucionFPGreedy, tiempoEjecucionBLAleatorio, tiempoEjecucionBLGreedy;
        File carpeta = new File(fuenteDatos+"/Instancias");
        String[] grafos = carpeta.list();
        //PASO 1: Se obtiene la información de la fuente de datos y se genera el grafo.
        if(grafos != null){
            for(String g: grafos){
                String g2 = g.replaceAll(".txt","");
                System.out.println("Grafo "+g2);
                System.out.println("\tCreando el grafo....");
                FileReader flujoLectura = new FileReader(fuenteDatos+"/Instancias/"+g);
                BufferedReader lectura = new BufferedReader(flujoLectura);
                lineaLeida = 1;
                String informacionLeida;
                while((informacionLeida = lectura.readLine()) != null){
                    informacion1 = informacionLeida.split(" ");
                    informacion2 = new String[informacion1.length-1];
                    System.arraycopy(informacion1, 1, informacion2, 0, informacion2.length);
                    if(lineaLeida == 1){
                        numeroNodos = Integer.parseInt(informacion2[0]);
                        numeroInstalaciones = Integer.parseInt(informacion2[2]);
                        grafo = new Grafo(numeroNodos, numeroInstalaciones);
                    }else{
                        nodoA = Integer.parseInt(informacion2[0])-1;
                        nodoB = Integer.parseInt(informacion2[1])-1;
                        distnaciaAB = Integer.parseInt(informacion2[2]);
                        grafo.modificarDistancia(nodoA, nodoB, distnaciaAB);
                    }
                    lineaLeida++;
                }
                lectura.close();
                flujoLectura.close();

                if(grafo != null){
                    //PASO 2: Se genera el Frente de Pareto (ante y después de la BL) con y sin método Greedy en la elección de instalaciones.
                    grafo.calcularDistancias();
                    //PASO 2.1 (antes de BL): Sin método Greedy en la elección de instalaciones (instalaciones aleatorias).
                    System.out.println("\tGenerando el Frente de Pareto Aleatorio (antes de BL)....");
                    tiempoEjecucionFPAleatorio = grafo.FrenteParetoAleatorio()/1000;
                    solucionesFPAleatorio = (ArrayList<Solucion>) grafo.getFPAleatorio().clone();
                    numeroSolucionesFPAleatorio = solucionesFPAleatorio.size();
                    //PASO 2.2 (antes de BL): Con método Greedy en la elección de instalaciones (mejores instalaciones de partida).
                    System.out.println("\tGenerando el Frente de Pareto Greedy (antes de BL)....");
                    tiempoEjecucionFPGreedy = grafo.FrenteParetoGreedy()/1000;
                    solucionesFPGreedy = (ArrayList<Solucion>) grafo.getFPGreedy().clone();
                    numeroSolucionesFPGreedy = solucionesFPGreedy.size();
                    //PASO 2.3 (después de BL): Sin método Greedy en la elección de instalaciones (instalaciones aleatorias).
                    System.out.println("\tGenerando el Frente de Pareto Aleatorio (después de BL)....");
                    tiempoEjecucionBLAleatorio = grafo.BusquedaLocalGeneral(0)/1000;
                    solucionesBLAleatorio = (ArrayList<Solucion>) grafo.getFPAleatorio().clone();
                    numeroSolucionesBLAleatorio = solucionesBLAleatorio.size();
                    //PASO 2.4 (después de BL): Con método Greedy en la elección de instalaciones (mejores instalaciones de partida).
                    System.out.println("\tGenerando el Frente de Pareto Greedy (después de BL)....");
                    tiempoEjecucionBLGreedy = grafo.BusquedaLocalGeneral(1)/1000;
                    solucionesBLGreedy = (ArrayList<Solucion>) grafo.getFPGreedy().clone();
                    numeroSolucionesBLGreedy = solucionesBLGreedy.size();
                    //PASO 3: Se guarda la información de los resultados obtenidos en ficheros txt y png
                    try{
                        System.out.println("\tGenerando ficheros txt....");
                        //PASO 3.1: Se escribe un fichero txt con el resumen de cada grafo con su funcionamiento aleatorio y greedy.
                        FileOutputStream ficheroResumen = new FileOutputStream(fuenteDatos+"/Resumen/"+g);
                        BufferedWriter escritura = new BufferedWriter(new OutputStreamWriter(ficheroResumen, "UTF-8"));
                        escritura.write("Grafo "+g2+":\n");
                        escritura.write("\tFrente de Pareto Aleatorio (antes de la busqueda local):\n");
                        escritura.write("\t\tNumero de soluciones --> "+numeroSolucionesFPAleatorio+" soluciones.\n");
                        escritura.write("\t\tTiempo de ejecucion --> "+tiempoEjecucionFPAleatorio+" segundos.\n");
                        escritura.write("\tFrente de Pareto Greedy (antes de la busqueda local):\n");
                        escritura.write("\t\tNumero de soluciones --> "+numeroSolucionesFPGreedy+" soluciones.\n");
                        escritura.write("\t\tTiempo de ejecucion --> "+tiempoEjecucionFPGreedy+" segundos.\n");
                        escritura.write("\tFrente de Pareto Aleatorio (despues de la busqueda local):\n");
                        escritura.write("\t\tNumero de soluciones --> "+numeroSolucionesBLAleatorio+" soluciones.\n");
                        escritura.write("\t\tTiempo de ejecucion --> "+tiempoEjecucionBLAleatorio+" segundos.\n");
                        escritura.write("\tFrente de Pareto Greedy (despues de la busqueda local):\n");
                        escritura.write("\t\tNumero de soluciones --> "+numeroSolucionesBLGreedy+" soluciones.\n");
                        escritura.write("\t\tTiempo de ejecucion --> "+tiempoEjecucionBLGreedy+" segundos.\n\n");
                        escritura.close();
                        ficheroResumen.close();
                        //PASO 3.2: Se escribe un fichero txt con las soluciones del Frente de Pareto aleatorio (antes de la búsqueda local).
                        FileOutputStream ficheroFPAleatorio = new FileOutputStream(fuenteDatos+"/FPAleatorio/"+g);
                        escritura = new BufferedWriter(new OutputStreamWriter(ficheroFPAleatorio, "UTF-8"));
                        for(Solucion s: solucionesFPAleatorio)
                            escritura.write(s.getPmedian()+"\t"+s.getPdispersion()+"\n");
                        escritura.close();
                        ficheroFPAleatorio.close();
                        //PASO 3.3: Se escribe un fichero txt con las soluciones del Frente de Pareto greedy (antes de la búsqueda local).
                        ficheroFPAleatorio = new FileOutputStream(fuenteDatos+"/FPGreedy/"+g);
                        escritura = new BufferedWriter(new OutputStreamWriter(ficheroFPAleatorio, "UTF-8"));
                        for(Solucion s: solucionesFPGreedy)
                            escritura.write(s.getPmedian()+"\t"+s.getPdispersion()+"\n");
                        escritura.close();
                        ficheroFPAleatorio.close();
                        //PASO 3.4: Se escribe un fichero txt con las soluciones del Frente de Pareto aleatorio (después de la búsqueda local).
                        ficheroFPAleatorio = new FileOutputStream(fuenteDatos+"/BLAleatorio/"+g);
                        escritura = new BufferedWriter(new OutputStreamWriter(ficheroFPAleatorio, "UTF-8"));
                        for(Solucion s: solucionesBLAleatorio)
                            escritura.write(s.getPmedian()+"\t"+s.getPdispersion()+"\n");
                        escritura.close();
                        ficheroFPAleatorio.close();
                        //PASO 3.5: Se escribe un fichero txt con las soluciones del Frente de Pareto greedy (después de la búsqueda local).
                        ficheroFPAleatorio = new FileOutputStream(fuenteDatos+"/BLGreedy/"+g);
                        escritura = new BufferedWriter(new OutputStreamWriter(ficheroFPAleatorio, "UTF-8"));
                        for(Solucion s: solucionesBLGreedy)
                            escritura.write(s.getPmedian()+"\t"+s.getPdispersion()+"\n");
                        escritura.close();
                        ficheroFPAleatorio.close();
                        //PASO 3.6: Se muestra un gráfico en un archivo png de Algoritmo que genera N soluciones random vs Algoritmo que genera N soluciones greedy
                        System.out.println("\tGenerando gráficas png....");
                        final XYSeries frenteParetoAleatorio = new XYSeries("Frente de Pareto Aleatorio");
                        for(Solucion s: solucionesFPAleatorio)
                            frenteParetoAleatorio.add(s.getPmedian(), s.getPdispersion());
                        final XYSeries frenteParetoGreedy = new XYSeries("Frente de Pareto Greedy");
                        for(Solucion s: solucionesFPGreedy)
                            frenteParetoGreedy.add(s.getPmedian(), s.getPdispersion());
                        XYSeriesCollection series = new XYSeriesCollection();
                        series.addSeries(frenteParetoAleatorio);
                        series.addSeries(frenteParetoGreedy);
                        JFreeChart grafica = new Grafica().crearGrafica(series, "Grafo "+g2+":\nFPAleatorio vs FPGreedy");
                        File graficaPNG = new File(fuenteDatos+"/Graficas FPAleatorio vs FPGreedy/"+g+".png");
                        ChartUtilities.saveChartAsPNG(graficaPNG, grafica, 400, 300);
                        //PASO 3.7: Se muestra un gráfico en un archivo png de Algoritmos que genera N soluciones random vs Algoritmo que genera N soluciones random con búsqueda local
                        final XYSeries busquedaLocalAleatorio = new XYSeries("Busqueda local Aleatorio");
                        for(Solucion s: solucionesBLAleatorio)
                            busquedaLocalAleatorio.add(s.getPmedian(), s.getPdispersion());
                        series = new XYSeriesCollection();
                        series.addSeries(frenteParetoAleatorio);
                        series.addSeries(busquedaLocalAleatorio);
                        grafica = new Grafica().crearGrafica(series, "Grafo "+g2+":\nFPAleatorio vs BLAleatorio");
                        graficaPNG = new File(fuenteDatos+"/Graficas FPAleatorio vs BLAleatorio/"+g+".png");
                        ChartUtilities.saveChartAsPNG(graficaPNG, grafica, 400, 300);
                        //PASO 3.8: Se muestra un gráfico en un archivo png de Algoritmo que genera N soluciones greedy vs Algoritmo que genera N soluciones greedy con búsqueda local
                        final XYSeries busquedaLocalGreedy = new XYSeries("Busqueda local Greedy");
                        for(Solucion s: solucionesBLGreedy)
                            busquedaLocalGreedy.add(s.getPmedian(), s.getPdispersion());
                        series = new XYSeriesCollection();
                        series.addSeries(frenteParetoGreedy);
                        series.addSeries(busquedaLocalGreedy);
                        grafica = new Grafica().crearGrafica(series, "Grafo "+g2+":\nFPGreedy vs BLGreedy");
                        graficaPNG = new File(fuenteDatos+"/Graficas FPGreedy vs BLGreedy/"+g+".png");
                        ChartUtilities.saveChartAsPNG(graficaPNG, grafica, 400, 300);
                        //PASO 3.9: Se muestra un gráfico en un archivo png de Algoritmo que genera N soluciones random con búsqueda local vs Algoritmo que genera N soluciones greedy con búsqueda local
                        series = new XYSeriesCollection();
                        series.addSeries(busquedaLocalAleatorio);
                        series.addSeries(busquedaLocalGreedy);
                        grafica = new Grafica().crearGrafica(series, "Grafo "+g2+":\nBLAleatorio vs BLGreedy");
                        graficaPNG = new File(fuenteDatos+"/Graficas BLAleatorio vs BLGreedy/"+g+".png");
                        ChartUtilities.saveChartAsPNG(graficaPNG, grafica, 400, 300);
                        System.out.println("\t....Fin de la ejecucion");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
