import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

public class Grafica {
    //Atributos
    private static Color FRENTE_PARETO = Color.blue;
    private static Color BUSQUEDA_LOCAL = Color.red;
    private static Color RECUADROS_GRAFICA = Color.black;
    private static Color FONDO_GRAFICA = Color.white;
    JFreeChart grafica;
    
    //Métodos
    private void configurarGuias(XYPlot plot){
        plot.setDomainGridlinePaint(RECUADROS_GRAFICA);
        plot.setRangeGridlinePaint(RECUADROS_GRAFICA);
    }

    private void configurar_eje_x(NumberAxis domainAxis){
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setTickUnit(new NumberTickUnit(0));
    }

    private void configurar_eje_y(NumberAxis rangeAxis){
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickUnit(new NumberTickUnit(0));
    }

    private void configurar_lineas(XYLineAndShapeRenderer renderer){
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesPaint(0, FRENTE_PARETO);
        renderer.setSeriesPaint(1, BUSQUEDA_LOCAL);
    }
    
    public JFreeChart crear_grafica(XYSeriesCollection series, String grafo){
        grafica = ChartFactory.createXYLineChart(grafo,"P-median","P-dispersion", series, PlotOrientation.VERTICAL,true,false,false);
        grafica.setBackgroundPaint(FONDO_GRAFICA);
        configurarGuias((XYPlot) grafica.getPlot());
        configurar_eje_x((NumberAxis) ((XYPlot) grafica.getPlot()).getDomainAxis());
        configurar_eje_y((NumberAxis) ((XYPlot) grafica.getPlot()).getDomainAxis());
        configurar_lineas((XYLineAndShapeRenderer) ((XYPlot) grafica.getPlot()).getRenderer());
        return grafica;
    }
}
