package QLearning;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QLearningMonitor {
	
	private String titulo=""; 
    private final List<Double> episodeRewards = new ArrayList<>();
    private final List<Double> movingAvgRewards = new ArrayList<>();
    private final List<Double> successRates = new ArrayList<>();
    private final List<Integer> episodeLengths = new ArrayList<>();
    private final List<Double> avgQValues = new ArrayList<>();
    private final List<Double> epsilons = new ArrayList<>();

    private int movingWindow = 50;

    private JFrame frame;
    private final List<ChartPanel> panels = new ArrayList<>();
    private final List<XYSeries> seriesList = new ArrayList<>();
    private boolean initialized = false;

    // --- Registro de métricas ---
    public void logEpisodeReward(double reward) {
        episodeRewards.add(reward);
        movingAvgRewards.add(computeMovingAverage(episodeRewards, movingWindow));
    }

    public void setTitulo(String titulo) {
    	this.titulo = titulo;
    }
    
    public void logSuccess(double success) { successRates.add(success); }

    public void logEpisodeLength(int length) { episodeLengths.add(length); }

    public void logAvgQ(double value) { avgQValues.add(value); }

    public void logEpsilon(double value) { epsilons.add(value); }

    public void update(double avgReward, double epsilon, double[][] Q, double successData) {
        logEpisodeReward(avgReward);
        logEpsilon(epsilon);
        logAvgQ(computeAverageQ(Q));
        logSuccess(successData);
        updateCharts();
    }

    private double computeMovingAverage(List<Double> data, int window) {
        if (data.isEmpty()) return 0;
        int start = Math.max(0, data.size() - window);
        double sum = 0;
        for (int i = start; i < data.size(); i++) sum += data.get(i);
        return sum / (data.size() - start);
    }

    private double computeAverageQ(double[][] Q) {
        double sum = 0;
        int count = 0;
        for (double[] row : Q)
            for (double q : row) {
                sum += q;
                count++;
            }
        return count == 0 ? 0 : sum / count;
    }

    // --- Inicializa ventana y gráficas ---
    public void initCharts() {
        if (initialized) return;
        frame = new JFrame("QLearning Monitor "+titulo+" (Live)");
        frame.setLayout(new GridLayout(3, 3));

        panels.add(createChartPanel("Recompensa total", episodeRewards, "Episodio", "Recompensa"));
        panels.add(createChartPanel("Recompensa media", movingAvgRewards, "Episodio", "Media"));
        panels.add(createChartPanel("Duración", toDoubleList(episodeLengths), "Episodio", "Pasos"));
        panels.add(createChartPanel("Valor Q", avgQValues, "Episodio", "Q medio"));
        panels.add(createChartPanel("Epsilon", epsilons, "Episodio", "ε"));
        panels.add(createChartPanel("Tasa de éxito", successRates, "Episodio", "Éxito (%)"));

        for (ChartPanel p : panels) frame.add(p);

        frame.pack();
        frame.setSize(600, 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialized = true;
    }

    private ChartPanel createChartPanel(String title, List<Double> data, String xLabel, String yLabel) {
        XYSeries series = new XYSeries(title);
        seriesList.add(series);
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset,
                PlotOrientation.VERTICAL, false, false, false);
        return new ChartPanel(chart);
    }

    // --- Actualiza dinámicamente ---
    private void updateCharts() {
        if (!initialized) return;
        SwingUtilities.invokeLater(() -> {
            updateSeries(seriesList.get(0), episodeRewards);
            updateSeries(seriesList.get(1), movingAvgRewards);
            updateSeries(seriesList.get(2), toDoubleList(episodeLengths));
            updateSeries(seriesList.get(3), avgQValues);
            updateSeries(seriesList.get(4), epsilons);
            updateSeries(seriesList.get(5), successRates);
        });
    }

    private void updateSeries(XYSeries series, List<Double> data) {
        series.clear();
        int start = Math.max(0, data.size() - 200); // rango visible reciente
        for (int i = start; i < data.size(); i++) {
            series.add(i, data.get(i));
        }
    }

    // --- Exportar todo a HTML interactivo ---
    public void exportToHTML(String path) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            writer.println("<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head><body>");
            writer.println("<h2>QLearning Monitor "+titulo+ " - Resultados</h2>");
            writePlot(writer, "Recompensa total", episodeRewards);
            writePlot(writer, "Recompensa media", movingAvgRewards);
            writePlot(writer, "Duración", toDoubleList(episodeLengths));
            writePlot(writer, "Valor Q medio", avgQValues);
            writePlot(writer, "Epsilon", epsilons);
            writePlot(writer, "Tasa de éxito", successRates);
            writer.println("</body></html>");
        } catch (IOException e) {
            System.err.println("Error exportando HTML: " + e.getMessage());
        }
    }

    private void writePlot(PrintWriter w, String title, List<Double> data) {
        w.println("<div id='" + title.replace(" ", "_") + "' style='width:600px;height:400px;'></div>");
        w.println("<script>");
        w.println("Plotly.newPlot('" + title.replace(" ", "_") + "', [{x:[" +
                range(0, data.size()) + "], y:[" + join(data) +
                "], mode:'lines', name:'" + title + "'}], {title:'" + title + "'});");
        w.println("</script>");
    }

    private String range(int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append(i).append(",");
        }
        return sb.toString();
    }

    private String join(List<Double> data) {
        StringBuilder sb = new StringBuilder();
        for (double d : data) {
            sb.append(String.format("%.5f", d)).append(",");
        }
        return sb.toString();
    }

    private List<Double> toDoubleList(List<Integer> list) {
        List<Double> res = new ArrayList<>();
        for (int i : list) res.add((double) i);
        return res;
    }
    
    
}
