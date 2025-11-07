package QLearning;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class QLearningMonitor extends JFrame {

    private static final int WINDOW_SIZE = 20;

    private XYSeries rewardSeries = new XYSeries("Reward medio");
    private XYSeries epsilonSeries = new XYSeries("Epsilon");

    private LinkedList<Double> rewardBuffer = new LinkedList<>();
    private LinkedList<Double> epsilonBuffer = new LinkedList<>();

    private int iteration = 0;

    // ✅ Q-table integrada
    private JTable qTable;
    private JScrollPane qScrollPane;
    private double[][] qValues;
    private Map<Point, Integer> lastUpdated = new HashMap<>();
    private int currentIteration = 0;
    private ColorRenderer colorRenderer = new ColorRenderer();
    

    public QLearningMonitor() {
        super("Monitor Q-Learning");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel superior con dos gráficas ---
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(createChartPanel(rewardSeries, "Recompensa media", "Iteraciones", "Reward"));
        topPanel.add(createChartPanel(epsilonSeries, "Epsilon", "Iteraciones", "ε"));
        add(topPanel, BorderLayout.NORTH);

        // --- Tabla Q ocupando todo el espacio inferior ---
        qTable = new JTable();
        qTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        qTable.setDefaultRenderer(Object.class, colorRenderer);
        qScrollPane = new JScrollPane(qTable);
        qScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        qScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(qScrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Crear gráfica con estilo limpio ---
    private ChartPanel createChartPanel(XYSeries series, String title, String xLabel, String yLabel) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset);

        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2f));
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    // --- Actualización principal del monitor ---
    public void update(double reward, double epsilon, double[][] qTableData) {
        iteration++;
        currentIteration = iteration;

        appendToBuffer(rewardBuffer, reward);
        appendToBuffer(epsilonBuffer, epsilon);

        updateSeries(rewardSeries, rewardBuffer);
        updateSeries(epsilonSeries, epsilonBuffer);

        updateQTable(qTableData);
    }

    private void appendToBuffer(LinkedList<Double> buffer, double value) {
        buffer.add(value);
        if (buffer.size() > WINDOW_SIZE)
            buffer.removeFirst();
    }

    private void updateSeries(XYSeries series, LinkedList<Double> buffer) {
        series.clear();
        int start = iteration - buffer.size() + 1;
        int i = 0;
        for (double v : buffer)
            series.add(start + i++, v);
    }

    // --- Actualiza la tabla Q en tiempo real ---
    private void updateQTable(double[][] newQTable) {
        if (newQTable == null || newQTable.length == 0) return;

        if (this.qValues == null ||
            this.qValues.length != newQTable.length ||
            this.qValues[0].length != newQTable[0].length) {
            this.qValues = deepCopy(newQTable);
            lastUpdated.clear();
        } else {
            for (int i = 0; i < newQTable.length; i++) {
                for (int j = 0; j < newQTable[i].length; j++) {
                    Point p = new Point(i, j);
                    if (Math.abs(newQTable[i][j] - this.qValues[i][j]) > 1e-6) {
                        lastUpdated.put(p, iteration);
                        colorRenderer.markVisited(p);
                    }
                }
            }
            this.qValues = deepCopy(newQTable);
        }

        updateVisibleTable();
    }

    private double[][] deepCopy(double[][] src) {
        double[][] copy = new double[src.length][];
        for (int i = 0; i < src.length; i++)
            copy[i] = Arrays.copyOf(src[i], src[i].length);
        return copy;
    }

    // --- Construye la tabla con scroll completo ---
    private void updateVisibleTable() {
        if (qValues == null) return;

        int totalTuples = qValues.length;
        int numColumns = qValues[0].length;

        String[] columnNames = new String[numColumns + 1];
        columnNames[0] = "Tupla #";
        for (int j = 0; j < numColumns; j++)
            columnNames[j + 1] = "A" + j;

        String[][] data = new String[totalTuples][numColumns + 1];
        for (int i = 0; i < totalTuples; i++) {
            data[i][0] = String.valueOf(i);
            for (int j = 0; j < numColumns; j++)
                data[i][j + 1] = String.format("%.3f", qValues[i][j]);
        }

        qTable.setModel(new DefaultTableModel(data, columnNames));
        qTable.repaint();
    }

    // --- Renderizado de color dinámico ---
    private class ColorRenderer extends DefaultTableCellRenderer {
        private Set<Point> visitedCells = new HashSet<>();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if (col == 0) {
                c.setBackground(Color.LIGHT_GRAY);
                return c;
            }

            if (qValues == null) return c;

            int tupleIndex = row;
            int attrIndex = col - 1;
            Point key = new Point(tupleIndex, attrIndex);

            if (!visitedCells.contains(key)) {
                c.setBackground(Color.RED);
                return c;
            }

            Integer last = lastUpdated.get(key);
            if (last == null) {
                c.setBackground(Color.WHITE);
                return c;
            }

            int diff = currentIteration - last;
            if (diff == 0)
                c.setBackground(Color.GREEN);
            else if (diff <= 2)
                c.setBackground(Color.YELLOW);
            else
                c.setBackground(Color.WHITE);

            return c;
        }

        public void markVisited(Point p) {
            visitedCells.add(p);
        }
    }
}
