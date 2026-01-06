package QLearning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import champ2011client.SensorModel;

public class MonitorGear extends JFrame {

    private static final long serialVersionUID = 1L;

    // Series de datos gráficos
    private XYSeries seriesRPM;
    private XYSeries seriesSpeed;
    private XYSeries seriesReward;
    private XYSeries seriesGear;

    private int timeStep = 0;
    private final int MAX_DATA_POINTS = 500;

    // --- VARIABLES PARA CSV ---
    private PrintWriter logWriter;
    private final String FOLDER_NAME = "Knowledge";
    private final String FILE_NAME = "Analisis_Gear.csv";

    public MonitorGear() {
        super("Monitor TORCS - QLearning Gearbox (CSV Recording)");

        // 1. INICIALIZAR EL ESCRITOR DE CSV
        initCSVWriter();

        // 2. INICIALIZAR SERIES GRÁFICAS
        seriesRPM = new XYSeries("RPM");
        seriesSpeed = new XYSeries("Velocidad (km/h)");
        seriesReward = new XYSeries("Recompensa");
        seriesGear = new XYSeries("Marcha");

        // 3. CREAR PANELES (Igual que antes)
        ChartPanel panelRPM = new ChartPanel(createChartRPM());
        ChartPanel panelSpeed = new ChartPanel(createChartSpeed());
        ChartPanel panelReward = new ChartPanel(createChartReward());
        ChartPanel panelGear = new ChartPanel(createChartGear());

        Dimension dim = new Dimension(400, 300);
        panelRPM.setPreferredSize(dim);
        panelSpeed.setPreferredSize(dim);
        panelReward.setPreferredSize(dim);
        panelGear.setPreferredSize(dim);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2));
        mainPanel.add(panelRPM);
        mainPanel.add(panelSpeed);
        mainPanel.add(panelReward);
        mainPanel.add(panelGear);

        this.setContentPane(mainPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        // Aseguramos que al cerrar la ventana se cierre el archivo también
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Hook de cierre para guardar datos si matas el proceso
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (logWriter != null) {
                logWriter.close();
                System.out.println("Archivo CSV cerrado correctamente.");
            }
        }));
    }

    private void initCSVWriter() {
        try {
            // A. Crear directorio si no existe
            File directory = new File(FOLDER_NAME);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // B. Crear el archivo
            File file = new File(directory, FILE_NAME);
            
            // C. Abrir flujo de escritura (false = sobrescribir archivo cada vez que arranques)
            logWriter = new PrintWriter(new FileWriter(file, false));
            
            // D. Escribir Cabecera
            logWriter.println("STEP;RPM;SPEED;GEAR;REWARD");
            logWriter.flush(); // Forzar escritura en disco
            
            System.out.println("Guardando logs en: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("ERROR CRÍTICO: No se pudo crear el archivo CSV.");
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE CREACIÓN DE GRÁFICOS (Sin cambios) ---
    private JFreeChart createChartRPM() {
        XYSeriesCollection dataset = new XYSeriesCollection(seriesRPM);
        JFreeChart chart = ChartFactory.createXYLineChart("Motor (RPM)", "Tiempo", "RPM", dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setAutoRange(true);
        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(true);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.RED);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        return chart;
    }

    private JFreeChart createChartSpeed() {
        XYSeriesCollection dataset = new XYSeriesCollection(seriesSpeed);
        JFreeChart chart = ChartFactory.createXYLineChart("Velocidad", "Tiempo", "km/h", dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setAutoRange(true);
        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(true);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        return chart;
    }

    private JFreeChart createChartReward() {
        XYSeriesCollection dataset = new XYSeriesCollection(seriesReward);
        JFreeChart chart = ChartFactory.createXYLineChart("Recompensa", "Tiempo", "Puntos", dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setAutoRange(true);
        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(0, 128, 0)); 
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        return chart;
    }

    private JFreeChart createChartGear() {
        XYSeriesCollection dataset = new XYSeriesCollection(seriesGear);
        JFreeChart chart = ChartFactory.createXYLineChart("Marcha", "Tiempo", "Gear", dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot plot = chart.getXYPlot();
        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setAutoRange(true);
        range.setAutoRangeIncludesZero(true);
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYStepRenderer renderer = new XYStepRenderer();
        renderer.setSeriesPaint(0, new Color(255, 140, 0)); 
        renderer.setBaseStroke(new java.awt.BasicStroke(2.0f)); 
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        return chart;
    }

    // --- MÉTODO UPDATE ---
    public void update(SensorModel sensors, double reward, int actionGear) {
        timeStep++;

        // 1. ESCRIBIR EN CSV
        if (logWriter != null) {
            // Usamos String.format con Locale.US si quieres puntos decimales (.) 
            // o default si prefieres comas (,) dependiendo de tu Excel.
            // Aquí uso formato estándar con punto decimal.
            logWriter.printf("%d;%.0f;%.0f;%d;%.2f%n", 
                timeStep, 
                sensors.getRPM(), 
                sensors.getSpeed(), 
                actionGear, 
                reward
            );
            
            // Hacemos flush cada vez para asegurar que si el programa crashea,
            // los datos se hayan guardado en el disco.
            logWriter.flush();
        }

        // 2. ACTUALIZAR GRÁFICAS
        SwingUtilities.invokeLater(() -> {
            seriesRPM.add(timeStep, sensors.getRPM());
            seriesSpeed.add(timeStep, sensors.getSpeed());
            seriesReward.add(timeStep, reward);
            seriesGear.add(timeStep, actionGear);

            if (seriesRPM.getItemCount() > MAX_DATA_POINTS) {
                seriesRPM.remove(0);
                seriesSpeed.remove(0);
                seriesReward.remove(0);
                seriesGear.remove(0);
            }
        });
    }
}