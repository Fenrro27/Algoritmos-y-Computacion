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

    private static final int WINDOW_SIZE = 20; // puntos visibles en las gráficas

    private XYSeries rewardSeries = new XYSeries("Reward medio");
    private XYSeries epsilonSeries = new XYSeries("Epsilon");
    private XYSeries qValueSeries = new XYSeries("Valor Q medio");
    private XYSeries optimalActionSeries = new XYSeries("Acción óptima %");

    private LinkedList<Double> rewardBuffer = new LinkedList<>();
    private LinkedList<Double> epsilonBuffer = new LinkedList<>();
    private LinkedList<Double> qBuffer = new LinkedList<>();
    private LinkedList<Double> optimalBuffer = new LinkedList<>();

    private int iteration = 0;

    private QTableWindow qTableWindow;

    public QLearningMonitor() {
        super("Monitor Q-Learning");
        setLayout(new GridLayout(2, 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        add(createChartPanel(rewardSeries, "Recompensa media", "Iteraciones", "Reward"));
        add(createChartPanel(epsilonSeries, "Epsilon", "Iteraciones", "ε"));
        add(createChartPanel(qValueSeries, "Valor Q medio", "Iteraciones", "Q promedio"));
        add(createChartPanel(optimalActionSeries, "% Acciones óptimas", "Iteraciones", "%"));

        // Ventana Q-table
        qTableWindow = new QTableWindow(this);
        qTableWindow.setVisible(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                qTableWindow.dispose();
            }
        });

        qTableWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

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
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2f));
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    public void update(double reward, double epsilon, double avgQ, double optimalPercent, double[][] qTable) {
        iteration++;

        appendToBuffer(rewardBuffer, reward);
        appendToBuffer(epsilonBuffer, epsilon);
        appendToBuffer(qBuffer, avgQ);
        appendToBuffer(optimalBuffer, optimalPercent);

        updateSeries(rewardSeries, rewardBuffer);
        updateSeries(epsilonSeries, epsilonBuffer);
        updateSeries(qValueSeries, qBuffer);
        updateSeries(optimalActionSeries, optimalBuffer);

        qTableWindow.updateTable(qTable, iteration);
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
        for (double v : buffer) {
            series.add(start + i, v);
            i++;
        }
    }
}

/*------------------------------------------------------------
 * Ventana Q-table con sliders y colores dinámicos
 *-----------------------------------------------------------*/
class QTableWindow extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;
    private JSlider tupleSlider;
    private JSlider attrSlider;
    private double[][] qTable;
    private double[][] lastQValues;
    private int visibleTuples = 20;
    private int startTuple = 0;
    private int selectedAttr = -1;

    private Map<Point, Integer> lastUpdated = new HashMap<>();
    private int currentIteration = 0;

    public QTableWindow(JFrame parent) {
        super("Tabla Q");
        setSize(1000, 700);
        setLocation(parent.getX() + parent.getWidth(), parent.getY());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        table = new JTable();
        table.setDefaultRenderer(Object.class, new ColorRenderer());
        scrollPane = new JScrollPane(table);

        tupleSlider = new JSlider(0, 0, 0);
        attrSlider = new JSlider(0, 0, 0);

        tupleSlider.addChangeListener(e -> {
            startTuple = tupleSlider.getValue();
            updateVisibleTable();
        });

        attrSlider.addChangeListener(e -> {
            selectedAttr = attrSlider.getValue();
            updateVisibleTable();
        });

        // ✅ Arreglo del layout del panel inferior
        JPanel controlPanel = new JPanel(new GridLayout(4, 1));
        controlPanel.add(new JLabel("Tuplas visibles (scroll):"));
        controlPanel.add(tupleSlider);
        controlPanel.add(new JLabel("Atributo seleccionado:"));
        controlPanel.add(attrSlider);

        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public void updateTable(double[][] newQTable, int iteration) {
        if (newQTable == null || newQTable.length == 0 || newQTable[0].length == 0)
            return;

        this.currentIteration = iteration;

        // ✅ Redimensiona completamente si cambia el tamaño de la Q
        if (this.qTable == null || this.qTable.length != newQTable.length || this.qTable[0].length != newQTable[0].length) {
            this.qTable = deepCopy(newQTable);
            this.lastQValues = deepCopy(newQTable);
            lastUpdated.clear(); // reiniciar cambios
        } else {
        	for (int i = 0; i < newQTable.length; i++) {
        	    for (int j = 0; j < newQTable[i].length; j++) {
        	        Point p = new Point(i, j);
        	        if (Math.abs(newQTable[i][j] - this.qTable[i][j]) > 1e-6) {
        	            lastUpdated.put(p, iteration);
        	            this.lastQValues[i][j] = this.qTable[i][j];
        	            // marcar como visitada
        	            ((ColorRenderer)table.getDefaultRenderer(Object.class)).markVisited(p);
        	        }
        	    }
        	}

            this.qTable = deepCopy(newQTable);
        }

        int maxTuple = Math.max(0, qTable.length - visibleTuples);
        tupleSlider.setMaximum(maxTuple);
        attrSlider.setMaximum(qTable[0].length - 1);

        // ✅ Esto ahora actualiza correctamente las 300 filas
        updateVisibleTable();
    }

    private double[][] deepCopy(double[][] src) {
        double[][] copy = new double[src.length][];
        for (int i = 0; i < src.length; i++)
            copy[i] = Arrays.copyOf(src[i], src[i].length);
        return copy;
    }

    private void updateVisibleTable() {
        if (qTable == null) return;

        int totalTuples = qTable.length;
        int numColumns = qTable[0].length;

        // ✅ Verifica límites y evita errores negativos
        if (startTuple < 0) startTuple = 0;
        if (startTuple >= totalTuples) startTuple = totalTuples - visibleTuples;
        if (startTuple < 0) startTuple = 0;

        int visible = Math.min(visibleTuples, totalTuples - startTuple);

        String[] columnNames = new String[numColumns + 1];
        columnNames[0] = "Tupla #";
        for (int j = 0; j < numColumns; j++)
            columnNames[j + 1] = "A" + j;

        String[][] data = new String[visible][numColumns + 1];
        for (int i = 0; i < visible; i++) {
            int tupleIndex = startTuple + i;
            data[i][0] = String.valueOf(tupleIndex);
            for (int j = 0; j < numColumns; j++) {
                data[i][j + 1] = String.format("%.3f", qTable[tupleIndex][j]);
            }
        }

        table.setModel(new DefaultTableModel(data, columnNames));
        table.repaint();
    }

    private class ColorRenderer extends DefaultTableCellRenderer {

        // Map para controlar si la celda ha sido visitada alguna vez
        private Set<Point> visitedCells = new HashSet<>();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if (col == 0) { // columna de índice
                c.setBackground(Color.LIGHT_GRAY);
                return c;
            }

            if (qTable == null) return c;

            int tupleIndex = startTuple + row;
            int attrIndex = col - 1;
            Point key = new Point(tupleIndex, attrIndex);

            // Si la celda nunca ha sido visitada, rojo
            if (!visitedCells.contains(key)) {
                c.setBackground(Color.RED);
                return c;
            }

            // Si la celda ha sido visitada, aplicar colores según la última actualización
            Integer last = lastUpdated.get(key);
            if (last == null) {
                c.setBackground(Color.WHITE);
                return c;
            }

            int diff = currentIteration - last;
            if (diff == 0) {
                c.setBackground(Color.GREEN);
            } else if (diff <= 2) {
                c.setBackground(Color.YELLOW);
            } else {
                c.setBackground(Color.WHITE);
            }

            return c;
        }

        // Método para marcar una celda como visitada
        public void markVisited(Point p) {
            visitedCells.add(p);
        }
    }
}