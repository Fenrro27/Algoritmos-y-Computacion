package QLearning;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class QLearningMonitor extends JFrame {

    private static final int WINDOW_SIZE = 20; // Número de puntos que se ven en la gráfica

    private XYSeries rewardSeries = new XYSeries("Reward medio");
    private XYSeries epsilonSeries = new XYSeries("Epsilon");
    private XYSeries qValueSeries = new XYSeries("Valor Q medio");
    private XYSeries optimalActionSeries = new XYSeries("Acción óptima %");

    private LinkedList<Double> rewardBuffer = new LinkedList<>();
    private LinkedList<Double> epsilonBuffer = new LinkedList<>();
    private LinkedList<Double> qBuffer = new LinkedList<>();
    private LinkedList<Double> optimalBuffer = new LinkedList<>();

    private int iteration = 0;

    public QLearningMonitor() {
        super("Monitor Q-Learning");
        setLayout(new GridLayout(2, 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        add(createChartPanel(rewardSeries, "Recompensa media", "Iteraciones", "Reward"));
        add(createChartPanel(epsilonSeries, "Epsilon", "Iteraciones", "ε"));
        add(createChartPanel(qValueSeries, "Valor Q medio", "Iteraciones", "Q promedio"));
        add(createChartPanel(optimalActionSeries, "% Acciones óptimas", "Iteraciones", "%"));

        setVisible(true);
    }

    private ChartPanel createChartPanel(XYSeries series, String title, String xLabel, String yLabel) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset);

        // Mejorar renderizado
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Fuentes legibles
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        // Renderer para líneas más gruesas
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2f));
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    public void update(double reward, double epsilon, double avgQ, double optimalPercent) {
        iteration++;

        // Actualizar buffers
        appendToBuffer(rewardBuffer, reward);
        appendToBuffer(epsilonBuffer, epsilon);
        appendToBuffer(qBuffer, avgQ);
        appendToBuffer(optimalBuffer, optimalPercent);

        // Actualizar series (deslizante)
        updateSeries(rewardSeries, rewardBuffer);
        updateSeries(epsilonSeries, epsilonBuffer);
        updateSeries(qValueSeries, qBuffer);
        updateSeries(optimalActionSeries, optimalBuffer);
    }

    private void appendToBuffer(LinkedList<Double> buffer, double value) {
        buffer.add(value);
        if (buffer.size() > WINDOW_SIZE) {
            buffer.removeFirst();
        }
    }

    private void updateSeries(XYSeries series, LinkedList<Double> buffer) {
        series.clear();
        int start = iteration - buffer.size() + 1;
        int i = 0;
        for (double v : buffer) {
            series.add(start + i, v);
            i++;
        }
    }
}
