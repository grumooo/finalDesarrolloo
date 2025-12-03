import javax.swing.*;
import java.awt.*;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import java.io.PrintWriter;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class clienteUI extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public clienteUI() {
        setTitle("Sistema de Monitoreo - Proyecto Final");
        ImageIcon icon = new ImageIcon(getClass().getResource("/escudo-med-lema.gif"));
        setIconImage(icon.getImage());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel panelInicio = crearPanelInicio();
        mainPanel.add(panelInicio, "INICIO");

        JPanel panelMonitor = new JPanel(new BorderLayout());

        grafica migrafica = new grafica();
        panelMonitor.add(migrafica, BorderLayout.CENTER);

        JPanel panelControles = new JPanel(new FlowLayout());

        SerialPort[] puertosDisponibles = SerialPort.getCommPorts();
        String[] nombresPuertos = new String[puertosDisponibles.length + 1];

        nombresPuertos[0] = "Simulación";
        for (int i = 0; i < puertosDisponibles.length; i++) {
            nombresPuertos[i + 1] = puertosDisponibles[i].getSystemPortName();
        }

        JComboBox<String> comboPuertos = new JComboBox<>(nombresPuertos);

        JButton btnAccion = new JButton("Iniciar");
        btnAccion.setFont(temaUI.FONT_BOTON);

        btnAccion.addActionListener(e -> {
            if (btnAccion.getText().equals("Iniciar")) {
                btnAccion.setText("Detener");
                // Iniciar la simulación en un hilo separado
                iniciarLecturaDatos(migrafica, (String) comboPuertos.getSelectedItem());
            } else {
                btnAccion.setText("Iniciar");
                detenerLecturaDatos();
            }
        });

        JButton btnVolver1 = new JButton("Volver");
        btnVolver1.addActionListener(e -> {
            detenerLecturaDatos();
            btnAccion.setText("Iniciar");
            cardLayout.show(mainPanel, "INICIO");
        });

        panelControles.add(new JLabel("Puerto:"));
        panelControles.add(comboPuertos);
        panelControles.add(btnAccion);
        panelControles.add(btnVolver1);

        panelMonitor.add(panelControles, BorderLayout.SOUTH);
        mainPanel.add(panelMonitor, "MONITOR");

        JPanel panelHistorico = new JPanel(new BorderLayout());

        JPanel panelFiltros = new JPanel();
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de búsqueda"));

        panelFiltros.add(new JLabel("Fecha:"));

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        DatePicker datePicker = new DatePicker(dateSettings);
        datePicker.setDateToToday();
        panelFiltros.add(datePicker);

        panelFiltros.add(new JLabel("Hora:"));
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.use24HourClockFormat();
        TimePicker timePicker = new TimePicker(timeSettings);
        panelFiltros.add(timePicker);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setFont(temaUI.FONT_BOTON);
        btnConsultar.setBackground(temaUI.DORADO_UNISON);
        panelFiltros.add(btnConsultar);

        panelHistorico.add(panelFiltros, BorderLayout.NORTH);

        grafica graficaHist = new grafica();
        panelHistorico.add(graficaHist, BorderLayout.CENTER);

        JPanel panelAbajo = new JPanel();
        JButton btnVolver2 = new JButton("Volver al Inicio");
        btnVolver2.addActionListener(e -> cardLayout.show(mainPanel, "INICIO"));
        panelAbajo.add(btnVolver2);
        panelHistorico.add(panelAbajo, BorderLayout.SOUTH);

        btnConsultar.addActionListener(e -> {
            graficaHist.limpiar();

            String fecha = datePicker.getDateStringOrEmptyString();
            String hora = timePicker.getTimeStringOrEmptyString();

            String filtroCombinado = fecha + ";" + hora;

            new Thread(() -> {
                btnConsultar.setEnabled(false);
                cargarDatosHistoricos(graficaHist, filtroCombinado);
                SwingUtilities.invokeLater(() -> btnConsultar.setEnabled(true));
            }).start();
        });
        mainPanel.add(panelHistorico, "HISTORICO");
        add(mainPanel);
    }

    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblLogo = new JLabel();

        try {
            java.net.URL imgURL = getClass().getResource("/escudo-med-lema.gif");
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);

                Image imagenEscalada = iconOriginal.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(imagenEscalada));
            } else {
                lblLogo.setText("Logo no encontrado");
            }
        } catch (Exception e) {
            lblLogo.setText("Error logo");
        }

        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(lblLogo, gbc);

        JLabel lblTitulo = new JLabel("SISTEMA DE MONITOREO");
        lblTitulo.setFont(temaUI.FONT_TITULO);
        lblTitulo.setForeground(temaUI.AZUL_OSCURO);

        gbc.gridy = 1;
        panel.add(lblTitulo, gbc);

        JLabel lblAutor = new JLabel("Creado por: Jose Miguel G. Falcon");
        lblAutor.setFont(temaUI.FONT_REGULAR);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(lblAutor, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);

        JButton btnMonitor = crearBotonEstilizado("Monitor", temaUI.AZUL_UNISON);
        btnMonitor.addActionListener(e -> cardLayout.show(mainPanel, "MONITOR"));

        JButton btnHistorico = crearBotonEstilizado("Histórico", temaUI.DORADO_UNISON);
        btnHistorico.addActionListener(e -> cardLayout.show(mainPanel, "HISTORICO"));

        panelBotones.add(btnMonitor);
        panelBotones.add(btnHistorico);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(panelBotones, gbc);

        return panel;
    }

    private JButton crearBotonEstilizado(String texto, Color colorFondo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(temaUI.FONT_BOTON);
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new clienteUI().setVisible(true);
        });
    }
    private Thread hiloLectura;
    private boolean leyendo = false;

    private void iniciarLecturaDatos(grafica g, String puertoSeleccionado) {
        leyendo = true;
        g.limpiar();

        hiloLectura = new Thread(() -> {


            if (puertoSeleccionado.equals("Simulación")) {
                System.out.println("Iniciando Modo Simulación...");
                java.util.Random rand = new java.util.Random();
                ejecutarBuclePrincipal(g, null, rand);

            }
            else {
                System.out.println("Conectando a Arduino en " + puertoSeleccionado + "...");
                SerialPort comPort = SerialPort.getCommPort(puertoSeleccionado);
                comPort.setBaudRate(9600);
                comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

                if (comPort.openPort()) {
                    System.out.println("Puerto abierto exitosamente.");
                    try {
                        Thread.sleep(2000);

                        Scanner scanner = new Scanner(comPort.getInputStream());
                        System.out.println(scanner.nextLine());
                        ejecutarBuclePrincipal(g, scanner, null);



                        scanner.close();
                    } catch (Exception ex) { ex.printStackTrace(); }
                    comPort.closePort();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "No se pudo abrir el puerto " + puertoSeleccionado);
                    leyendo = false;
                }
            }
        });
        hiloLectura.start();
    }

    private void ejecutarBuclePrincipal(grafica g, Scanner scannerArduino, java.util.Random randomSimulado) {
        // Conexión al Servidor
        try (java.net.Socket socket = new java.net.Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conectado al servidor. Iniciando transmisión...");

            while (leyendo) {
                int x = 0, y = 0, z = 0;
                boolean datosValidos = false;

                try {
                    if (scannerArduino != null) {
                        if (scannerArduino.hasNextLine()) {
                            String linea = scannerArduino.nextLine();
                            try {
                                String[] partes = linea.split(",");
                                x = Integer.parseInt(partes[0].split(":")[1].trim());
                                y = Integer.parseInt(partes[1].split(":")[1].trim());
                                z = Integer.parseInt(partes[2].split(":")[1].trim());
                                datosValidos = true;
                            } catch (Exception e) {
                                System.out.println("Formato incorrecto del Arduino: " + linea);
                            }
                        }
                    } else {
                        x = randomSimulado.nextInt(50);
                        y = randomSimulado.nextInt(50) + 50;
                        z = randomSimulado.nextInt(50) + 100;
                        datosValidos = true;
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    }

                    if (datosValidos) {
                        final int fx = x, fy = y, fz = z;
                        SwingUtilities.invokeLater(() -> g.agregarPunto(fx, fy, fz));

                        String datos = "x:" + x + ", y:" + y + ", z:" + z;
                        out.println(seguridad.encriptar(datos));

                        if (out.checkError()) {
                            System.out.println("Error: El servidor cerró la conexión.");
                            break;
                        }
                        System.out.println("Enviado: " + datos);
                    }

                } catch (Exception ex) {
                    System.out.println("Error leve en el ciclo: " + ex.getMessage());
                    try { Thread.sleep(100); } catch (InterruptedException i) {}
                }
            }
        } catch (Exception e) {
            System.out.println("Error crítico de conexión: " + e.getMessage());
        }
    }

    private void detenerLecturaDatos() {
        leyendo = false;
        if (hiloLectura != null) {
            hiloLectura.interrupt();
        }
    }
    private void cargarDatosHistoricos(grafica g, String filtroFecha) {
        try (java.net.Socket socket = new java.net.Socket("localhost", 5000);
             java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
             java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))) {

            String solicitud = "PEDIR_HISTORIAL";
            if (filtroFecha != null && !filtroFecha.isEmpty()) {
                solicitud += ":" + filtroFecha;
            }

            out.println(seguridad.encriptar(solicitud));

            String linea;
            while ((linea = in.readLine()) != null) {
                String datos = seguridad.desencriptar(linea);

                if (datos.equals("FIN_HISTORIAL")) break;

                try {
                    String[] partes = datos.split(",");
                    int x = 0, y = 0, z = 0;

                    for (String p : partes) {
                        p = p.trim();
                        if (p.startsWith("x:")) x = Integer.parseInt(p.split(":")[1]);
                        if (p.startsWith("y:")) y = Integer.parseInt(p.split(":")[1]);
                        if (p.startsWith("z:")) z = Integer.parseInt(p.split(":")[1]);
                    }

                    final int fx = x, fy = y, fz = z;
                    javax.swing.SwingUtilities.invokeLater(() -> g.agregarPunto(fx, fy, fz));

                } catch (Exception ex) {
                }
            }
            javax.swing.JOptionPane.showMessageDialog(this, "¡Historial cargado exitosamente!");

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Error cargando historial: " + e.getMessage());
        }
    }
}