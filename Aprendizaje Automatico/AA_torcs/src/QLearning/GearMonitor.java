package QLearning;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GearMonitor extends JFrame {

    private final XYSeries rpmSeries = new XYSeries("RPM");
    private final XYSeries gearSeries = new XYSeries("Gear");
    private final XYSeriesCollection rpmDataset = new XYSeriesCollection();
    private final XYSeriesCollection gearDataset = new XYSeriesCollection();

    private final XYPlot rpmPlot;
    private final XYPlot gearPlot;
    private final NumberAxis domainAxis;
    private static final int WINDOW = 400;

    // --- Tabla de estados Q ---
    private final DefaultTableModel tableModel;
    private final JTable qTable;

    public GearMonitor() {
        rpmDataset.addSeries(rpmSeries);
        gearDataset.addSeries(gearSeries);

        // --- Gráfico RPM ---
        XYLineAndShapeRenderer rpmRenderer = new XYLineAndShapeRenderer(true, false);
        rpmRenderer.setSeriesPaint(0, Color.RED);
        rpmPlot = new XYPlot(rpmDataset, null, new NumberAxis("RPM"), rpmRenderer);
        rpmPlot.getRangeAxis().setRange(0, 10000);

        // --- Gráfico Gear ---
        XYLineAndShapeRenderer gearRenderer = new XYLineAndShapeRenderer(true, false);
        gearRenderer.setSeriesPaint(0, Color.BLUE);
        gearPlot = new XYPlot(gearDataset, null, new NumberAxis("Gear"), gearRenderer);
        gearPlot.getRangeAxis().setRange(0.5, 6.5);

        // --- Eje X compartido ---
        domainAxis = new NumberAxis("Iteration");
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(domainAxis);
        combinedPlot.add(rpmPlot, 1);
        combinedPlot.add(gearPlot, 1);
        combinedPlot.setGap(10);

        JFreeChart chart = new JFreeChart("Q-Learning Gear Monitor", JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        // --- Tabla Q ---
        tableModel = new DefaultTableModel(new Object[]{"Estado", "Valores Q"}, 0);
        qTable = new JTable(tableModel);
        qTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        qTable.setRowHeight(20);
        JScrollPane scrollPane = new JScrollPane(qTable);
        scrollPane.setPreferredSize(new Dimension(800, 200));

        // --- Dividir vista ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, scrollPane);
        splitPane.setResizeWeight(0.7);

        setContentPane(splitPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public void updateData(int iteration, double rpm, int gear) {
        rpmSeries.add(iteration, rpm);
        gearSeries.add(iteration, gear);

        if (iteration > WINDOW) {
            rpmSeries.remove(0);
            gearSeries.remove(0);
            domainAxis.setRange(iteration - WINDOW, iteration);
        } else {
            domainAxis.setRange(0, WINDOW);
        }
    }

    // --- Actualiza la tabla Q con texto del estado y sus valores ---
    public void updateData(List<String> estados, List<String> valoresQ) {
        tableModel.setRowCount(0); // limpiar
        for (int i = 0; i < estados.size(); i++) {
            tableModel.addRow(new Object[]{estados.get(i), valoresQ.get(i)});
        }
    }
}
