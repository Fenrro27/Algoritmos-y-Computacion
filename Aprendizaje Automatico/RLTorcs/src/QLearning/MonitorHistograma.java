package QLearning;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MonitorHistograma extends JFrame {

    // --- DATOS ---
    private int[] stateVisitsTotal; // Visitas totales por estado
    private int[][] actionCountsPerState; // [EstadoID][AccionID] -> Cantidad
    private int numStates;
    private int numActions;
    private int estadoSeleccionado = -1; // Qué estado estamos viendo en detalle

    // --- GUI ---
    private CardLayout cardLayout;
    private JPanel mainPanelCards;
    private PanelBarrasGenerico panelEstadosScrollable;
    private PanelBarrasGenerico panelAccionesFijo;
    private JLabel labelTituloAcciones;

    // Configuración visual constante
    private final int ANCHO_BARRA_ESTADO = 25;
    private final int ESPACIO_ESTADO = 5;
    private final int MARGEN_X_INICIAL = 10;

    public MonitorHistograma(int numStates, int numActions, String titulo) {
        super(titulo);
        this.numStates = numStates;
        this.numActions = numActions;
        
        // 1. Inicializar Datos
        this.stateVisitsTotal = new int[numStates];
        this.actionCountsPerState = new int[numStates][numActions];

        // 2. Configurar Ventana Base
        this.setSize(900, 500);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // 3. Configurar el sistema de "Cartas" (CardLayout)
        cardLayout = new CardLayout();
        mainPanelCards = new JPanel(cardLayout);
        this.add(mainPanelCards);

        // --- CARTA 1: VISTA GLOBAL DE ESTADOS (Con Scroll) ---
        panelEstadosScrollable = new PanelBarrasGenerico(true);
        JScrollPane scrollPaneEstados = new JScrollPane(panelEstadosScrollable);
        scrollPaneEstados.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneEstados.getHorizontalScrollBar().setUnitIncrement(20);
        
        // AÑADIR DETECTOR DE CLICKS AL PANEL DE ESTADOS
        panelEstadosScrollable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                procesarClickEnEstado(e.getX());
            }
        });

        // --- CARTA 2: VISTA DETALLADA DE ACCIONES (Fija, sin scroll lateral) ---
        JPanel panelContenedorAcciones = new JPanel(new BorderLayout());
        
        // Cabecera con botón Volver y Título
        JPanel headerAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVolver = new JButton("<- Volver a Vista Global");
        btnVolver.addActionListener(e -> mostrarVistaGlobal());
        labelTituloAcciones = new JLabel("Detalle Acciones");
        labelTituloAcciones.setFont(new Font("Arial", Font.BOLD, 14));
        headerAcciones.add(btnVolver);
        headerAcciones.add(Box.createHorizontalStrut(20));
        headerAcciones.add(labelTituloAcciones);
        
        panelAccionesFijo = new PanelBarrasGenerico(false); // False = no usa ancho calculado para scroll
        
        panelContenedorAcciones.add(headerAcciones, BorderLayout.NORTH);
        panelContenedorAcciones.add(panelAccionesFijo, BorderLayout.CENTER);

        // 4. Añadir las cartas al panel principal
        mainPanelCards.add(scrollPaneEstados, "VISTA_ESTADOS");
        mainPanelCards.add(panelContenedorAcciones, "VISTA_ACCIONES");

        // Mostrar la primera carta por defecto
        cardLayout.show(mainPanelCards, "VISTA_ESTADOS");
    }

    /**
     * NUEVO MÉTODO: Ahora recibe el estado Y la acción tomada.
     */
    public void registrarEvento(int state, int actionIndex) {
        if (state >= 0 && state < numStates && actionIndex >= 0 && actionIndex < numActions) {
            // 1. Actualizar datos globales
            stateVisitsTotal[state]++;
            // 2. Actualizar matriz detallada
            actionCountsPerState[state][actionIndex]++;
            
            // 3. Decidir qué panel necesita repintarse
            if (mainPanelCards.getComponent(0).isVisible()) {
                // Si estamos viendo la global, actualizar sus datos y repintar
                panelEstadosScrollable.setDatos(stateVisitsTotal, stateVisitsTotal.length);
                panelEstadosScrollable.repaint();
            } else if (estadoSeleccionado == state) {
                // Si estamos viendo el detalle del estado que acaba de cambiar, repintar
                panelAccionesFijo.setDatos(actionCountsPerState[state], numActions);
                panelAccionesFijo.repaint();
            }
        }
    }

    // --- Lógica de Navegación ---

    private void procesarClickEnEstado(int mouseX) {
        // Matemática inversa: Convertir píxel X en índice de barra
        // x = MARGEN + (idx * (ANCHO + ESPACIO))
        // idx = (x - MARGEN) / (ANCHO + ESPACIO)
        
        int anchoTotalBarra = ANCHO_BARRA_ESTADO + ESPACIO_ESTADO;
        int indiceClicado = (mouseX - MARGEN_X_INICIAL) / anchoTotalBarra;

        // Verificar si el click fue válido en una barra existente
        if (indiceClicado >= 0 && indiceClicado < numStates) {
             mostrarVistaDetalleAcciones(indiceClicado);
        }
    }

    private void mostrarVistaDetalleAcciones(int stateIdx) {
        this.estadoSeleccionado = stateIdx;
        labelTituloAcciones.setText("Distribución de Acciones en ESTADO " + stateIdx + " (Total visitas: " + stateVisitsTotal[stateIdx] + ")");
        
        // Cargar los datos de ese estado en el panel de acciones
        panelAccionesFijo.setDatos(actionCountsPerState[stateIdx], numActions);
        
        // Cambiar la carta
        cardLayout.show(mainPanelCards, "VISTA_ACCIONES");
    }

    private void mostrarVistaGlobal() {
        this.estadoSeleccionado = -1;
        // Asegurar que los datos estén frescos
        panelEstadosScrollable.setDatos(stateVisitsTotal, numStates);
        cardLayout.show(mainPanelCards, "VISTA_ESTADOS");
    }


    // ==============================================================================
    // CLASE INTERNA GENERICA PARA DIBUJAR BARRAS (Reutilizable para ambos histogramas)
    // ==============================================================================
    private class PanelBarrasGenerico extends JPanel {
        private int[] datosADibujar;
        private int cantidadElementos;
        private int maxValor = 1;
        private boolean modoScrollable;

        final int MARGEN_SUPERIOR = 30;
        final int MARGEN_INFERIOR = 40;

        public PanelBarrasGenerico(boolean modoScrollable) {
            this.modoScrollable = modoScrollable;
            // Datos iniciales vacíos para que no falle al arrancar
            this.datosADibujar = new int[0];
        }

        public void setDatos(int[] datos, int cantidad) {
            this.datosADibujar = datos;
            this.cantidadElementos = cantidad;
            // Recalcular máximo local para escalar
            this.maxValor = 1;
            for (int d : datos) {
                if (d > maxValor) maxValor = d;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            if (modoScrollable) {
                // Ancho calculado para el scroll
                int anchoTotal = MARGEN_X_INICIAL + (cantidadElementos * (ANCHO_BARRA_ESTADO + ESPACIO_ESTADO)) + 50;
                return new Dimension(anchoTotal, 400);
            } else {
                // Ancho dinámico si no es scrollable
                return super.getPreferredSize(); 
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datosADibujar == null || datosADibujar.length == 0) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int areaDibujoAlto = h - MARGEN_SUPERIOR - MARGEN_INFERIOR;

            // Eje X base
            g2.setColor(Color.BLACK);
            g2.drawLine(10, h - MARGEN_INFERIOR, w - 10, h - MARGEN_INFERIOR);
            
            // Info de escala
            g2.drawString("Escala Max Y: " + maxValor, 10, 20);

            // Si no es scrollable, calculamos el ancho dinámicamente para que quepan todas
            int anchoBarraReal = ANCHO_BARRA_ESTADO;
            int espacioReal = ESPACIO_ESTADO;
            
            if (!modoScrollable && cantidadElementos > 0) {
                int espacioDisponible = w - (2 * MARGEN_X_INICIAL);
                anchoBarraReal = (espacioDisponible / cantidadElementos) - 2; // -2 de margen entre barras
                if (anchoBarraReal < 5) anchoBarraReal = 5; // Mínimo de seguridad
                espacioReal = 2;
            }

            for (int i = 0; i < cantidadElementos; i++) {
                // Seguridad por si el array de datos es menor que la cantidad esperada
                if(i >= datosADibujar.length) break; 
                
                int count = datosADibujar[i];

                // Coordenadas
                int x = MARGEN_X_INICIAL + (i * (anchoBarraReal + espacioReal));
                int alturaBarra = (int) (((double) count / maxValor) * areaDibujoAlto);
                if (count > 0 && alturaBarra == 0) alturaBarra = 1; // Mínimo visible
                int y = (h - MARGEN_INFERIOR) - alturaBarra;

                // Dibujar Barra
                if (count > 0) {
                    // Color diferente según el modo para diferenciarlos visualmente
                    g2.setColor(modoScrollable ? new Color(50, 100, 200) : new Color(200, 80, 50)); 
                    g2.fillRect(x, y, anchoBarraReal, alturaBarra);
                } else if (modoScrollable) {
                    // Marca gris para estados vacíos solo en la vista global
                    g2.setColor(new Color(230, 230, 230));
                    g2.fillRect(x, h - MARGEN_INFERIOR - 2, anchoBarraReal, 2);
                }

                // Etiquetas Eje X
                g2.setColor(Color.DARK_GRAY);
                String label = String.valueOf(i);
                // Si hay muchas acciones, solo pintar algunas etiquetas en el detalle
                if (!modoScrollable && cantidadElementos > 10 && i % 2 != 0) {
                    // saltar etiqueta para que no se amontonen
                } else {
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = x + (anchoBarraReal - fm.stringWidth(label)) / 2;
                    g2.drawString(label, textX, h - MARGEN_INFERIOR + 15);
                }
                
                // Valor sobre la barra (útil para el detalle de acciones)
                if(!modoScrollable && count > 0) {
                     g2.setColor(Color.BLACK);
                     FontMetrics fm = g2.getFontMetrics();
                     int valX = x + (anchoBarraReal - fm.stringWidth(String.valueOf(count))) / 2;
                     g2.drawString(String.valueOf(count), valX, y - 2);
                }
            }
        }
    }
    
    
}