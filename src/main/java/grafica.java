import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class grafica extends JPanel {

    private XYSeries serieX;
    private XYSeries serieY;
    private XYSeries serieZ;
    private int tiempo = 0;

    public grafica() {
        setLayout(new BorderLayout());

        serieX = new XYSeries("Sensor X");
        serieY = new XYSeries("Sensor Y");
        serieZ = new XYSeries("Sensor Z");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieX);
        dataset.addSeries(serieY);
        dataset.addSeries(serieZ);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Datos del Sensor en Tiempo Real",
                "Tiempo (s)",
                "Valor",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setDomainGridlinePaint(Color.LIGHT_GRAY);
        chart.getXYPlot().setRangeGridlinePaint(Color.LIGHT_GRAY);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        add(chartPanel, BorderLayout.CENTER);
    }

    public void agregarPunto(int x, int y, int z) {
        tiempo++; // Avanzamos un segundo
        serieX.add(tiempo, x);
        serieY.add(tiempo, y);
        serieZ.add(tiempo, z);

        if (tiempo > 100) {
            serieX.remove(0);
            serieY.remove(0);
            serieZ.remove(0);
        }
    }

    public void limpiar() {
        serieX.clear();
        serieY.clear();
        serieZ.clear();
        tiempo = 0;
    }
}