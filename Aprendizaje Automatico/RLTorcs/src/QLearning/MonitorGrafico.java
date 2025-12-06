package QLearning;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class MonitorGrafico extends JFrame {

    private XYSeries serieEpsilon;
    private XYSeries serieReward;
    private int iteracion = 0;

    public MonitorGrafico(String title) {
        super(title);

        // 1. Crear las series de datos
        serieEpsilon = new XYSeries("Epsilon");
        serieReward = new XYSeries("Recompensa");

        // --- EL REQUISITO DE LOS 20 DATOS ---
        // Esto hace que JFreeChart borre automáticamente los datos viejos
        serieEpsilon.setMaximumItemCount(1000);
        serieReward.setMaximumItemCount(1000);

        // 2. Crear las colecciones
        XYSeriesCollection datasetReward = new XYSeriesCollection(serieReward);
        XYSeriesCollection datasetEpsilon = new XYSeriesCollection(serieEpsilon);

        // 3. Crear el Gráfico base (Usamos Reward como eje principal)
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Evolución del Entrenamiento", // Título
                "Iteración (Tiempo)",          // Eje X
                "Recompensa",                  // Eje Y Principal
                datasetReward                  // Datos Principales
        );

        // 4. Configuración Avanzada (Doble Eje Y)
        XYPlot plot = chart.getXYPlot();

        //NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //    rangeAxis.setAutoRange(false); 
        // Fijamos el rango estricto de -20 a 20
        //rangeAxis.setRange(-20.0, 20.0); // <--- AQUÍ ESTÁ LA MAGIA
        
        // --- EJE SECUNDARIO PARA EPSILON (Derecha) ---
        NumberAxis axis2 = new NumberAxis("Epsilon");
        plot.setRangeAxis(1, axis2); // Índice 1 es el eje secundario
        plot.setDataset(1, datasetEpsilon); // Asignamos los datos de epsilon al índice 1
        plot.mapDatasetToRangeAxis(1, 1); // Mapeamos datos 1 al eje 1

        // 5. Estilos (Renderers)
        // Estilo para Recompensa (Azul, Línea sólida)
        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
        renderer1.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(0, renderer1);

        // Estilo para Epsilon (Rojo, Punteado o sólido)
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        renderer2.setSeriesPaint(0, Color.RED);
        plot.setRenderer(1, renderer2);

        // 6. Panel y Ventana
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void agregarDato(double epsilon, double reward) {
        // SwingUtilities asegura que la GUI se actualice en el hilo correcto
        SwingUtilities.invokeLater(() -> {
            iteracion++;
            serieEpsilon.add(iteracion, epsilon);
            serieReward.add(iteracion, reward);
        });
    }
}