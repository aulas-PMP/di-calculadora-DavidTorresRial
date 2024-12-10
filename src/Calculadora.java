import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculadora extends JFrame {
    private JTextField pantallaActual, pantallaAlmacenada;
    private String operacionActual = "";
    private double resultado = 0;
    private boolean nuevoNumero = true;
    private JLabel modoEntrada;
    private String modo = "Ambos";  // Modo inicial

    public Calculadora() {
        setTitle("Calculadora - David Torres");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración inicial de la ventana
        configurarVentana();

        // Configuración del Layout principal
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Panel de pantallas
        JPanel panelPantallas = configurarPanelPantallas();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        add(panelPantallas, gbc);

        // Panel de botones numéricos
        JPanel panelBotones = configurarPanelBotones();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        add(panelBotones, gbc);

        // Panel de operaciones
        JPanel panelOperaciones = configurarPanelOperaciones();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        add(panelOperaciones, gbc);

        // Panel de modo de entrada
        JPanel panelModo = configurarPanelModo();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        add(panelModo, gbc);

        // Configurar atajos de teclado
        configurarAtajosDeTeclado();

        setVisible(true);
    }

    private void configurarVentana() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.5), 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private JPanel configurarPanelPantallas() {
        JPanel panelPantallas = new JPanel(new GridLayout(2, 1, 5, 5));
        panelPantallas.setBackground(Color.WHITE);

        pantallaAlmacenada = new JTextField();
        pantallaActual = new JTextField("0");
        pantallaAlmacenada.setEditable(false);
        pantallaActual.setEditable(false);

        pantallaAlmacenada.setFont(new Font("Consolas", Font.PLAIN, 18));
        pantallaActual.setFont(new Font("Consolas", Font.BOLD, 24));

        pantallaAlmacenada.setBackground(Color.DARK_GRAY);
        pantallaActual.setBackground(Color.DARK_GRAY);
        pantallaAlmacenada.setForeground(Color.WHITE);
        pantallaActual.setForeground(Color.WHITE);

        panelPantallas.add(pantallaAlmacenada);
        panelPantallas.add(pantallaActual);

        return panelPantallas;
    }

    private JPanel configurarPanelBotones() {
        JPanel panelBotones = new JPanel(new GridLayout(4, 4, 5, 5));
        panelBotones.setBackground(new Color(240, 240, 240));

        for (int i = 1; i <= 9; i++) {
            agregarBotonNumerico(panelBotones, String.valueOf(i));
        }
        agregarBotonNumerico(panelBotones, "0");
        agregarBotonNumerico(panelBotones, ",");

        JButton botonModo = new JButton("Cambiar Modo");
        botonModo.setBackground(new Color(255, 204, 153));
        botonModo.setFont(new Font("Arial", Font.PLAIN, 14));
        botonModo.addActionListener(e -> cambiarModo());
        panelBotones.add(botonModo);

        return panelBotones;
    }

    private JPanel configurarPanelOperaciones() {
        JPanel panelOperaciones = new JPanel(new GridLayout(3, 2, 5, 5));
        panelOperaciones.setBackground(new Color(220, 220, 220));

        agregarBotonOperacion(panelOperaciones, "+");
        agregarBotonOperacion(panelOperaciones, "-");
        agregarBotonOperacion(panelOperaciones, "*");
        agregarBotonOperacion(panelOperaciones, "/");
        agregarBotonEspecial(panelOperaciones, "C", e -> reiniciar());
        agregarBotonEspecial(panelOperaciones, "=", e -> calcularResultado());

        return panelOperaciones;
    }

    private JPanel configurarPanelModo() {
        JPanel panelModo = new JPanel();
        panelModo.setBackground(Color.DARK_GRAY);

        modoEntrada = new JLabel("Modo: Ambos");
        modoEntrada.setFont(new Font("Arial", Font.ITALIC, 14));
        modoEntrada.setForeground(Color.WHITE);

        panelModo.add(modoEntrada);
        return panelModo;
    }

    private void configurarAtajosDeTeclado() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        for (int i = 0; i <= 9; i++) {
            final int num = i;
            inputMap.put(KeyStroke.getKeyStroke("NUMPAD" + i), "numpad" + i);
            actionMap.put("numpad" + i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (modo.equals("Ambos") || modo.equals("Teclado")) {
                        ingresarNumero(String.valueOf(num));
                    }
                }
            });
        }

        inputMap.put(KeyStroke.getKeyStroke("DECIMAL"), "decimal");
        actionMap.put("decimal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modo.equals("Ambos") || modo.equals("Teclado")) {
                    ingresarNumero(",");
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "borrarTodo");
        actionMap.put("borrarTodo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciar();
            }
        });

        String[] operaciones = { "ADD", "SUBTRACT", "MULTIPLY", "DIVIDE" };
        String[] simbolos = { "+", "-", "*", "/" };

        for (int i = 0; i < operaciones.length; i++) {
            final String operacion = simbolos[i];
            inputMap.put(KeyStroke.getKeyStroke(operaciones[i]), operacion);
            actionMap.put(operacion, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (modo.equals("Ambos") || modo.equals("Teclado")) {
                        setOperacion(operacion);
                    }
                }
            });
        }

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "calcular");
        actionMap.put("calcular", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modo.equals("Ambos") || modo.equals("Teclado")) {
                    calcularResultado();
                }
            }
        });
    }

    private void agregarBotonNumerico(JPanel panel, String texto) {
        JButton boton = crearBoton(texto, new Color(230, 230, 230), Color.BLACK);
        boton.addActionListener(e -> {
            if (modo.equals("Ambos") || modo.equals("Ratón")) {
                ingresarNumero(texto);
            }
        });
        panel.add(boton);
    }

    private void agregarBotonOperacion(JPanel panel, String texto) {
        JButton boton = crearBoton(texto, new Color(255, 204, 153), Color.BLACK);
        boton.addActionListener(e -> {
            if (modo.equals("Ambos") || modo.equals("Ratón")) {
                setOperacion(texto);
            }
        });
        panel.add(boton);
    }

    private void agregarBotonEspecial(JPanel panel, String texto, ActionListener action) {
        JButton boton = crearBoton(texto, new Color(255, 102, 102), Color.WHITE);
        boton.addActionListener(action);
        panel.add(boton);
    }

    private JButton crearBoton(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Consolas", Font.BOLD, 18));
        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return boton;
    }

    private void ingresarNumero(String texto) {
        if (texto.equals(",")) {
            if (pantallaActual.getText().contains(",")) return;
        }
        if (nuevoNumero) {
            pantallaActual.setText(texto.equals(",") ? "0," : texto);
            nuevoNumero = false;
        } else {
            pantallaActual.setText(pantallaActual.getText() + texto);
        }
        verificarNegativo();
    }

    private void setOperacion(String operacion) {
        try {
            if (!operacionActual.isEmpty() && !nuevoNumero) {
                calcularResultado(); // Si ya hay una operación en curso, la calculamos antes de empezar una nueva
            }
            resultado = Double.parseDouble(pantallaActual.getText().replace(",", "."));
            operacionActual = operacion; // Guardamos la operación actual
            pantallaAlmacenada.setText(resultado + " " + operacion); // Mostramos la operación en pantalla
            nuevoNumero = true;
        } catch (Exception e) {
            pantallaActual.setText("Error");
        }
    }
    
    private void calcularResultado() {
        try {
            double numActual = Double.parseDouble(pantallaActual.getText().replace(",", "."));
            switch (operacionActual) {
                case "+" -> resultado += numActual;
                case "-" -> resultado -= numActual;
                case "*" -> resultado *= numActual;
                case "/" -> {
                    if (numActual == 0) {
                        pantallaActual.setText("Error");
                        return;
                    }
                    resultado /= numActual;
                }
            }
            pantallaActual.setText(String.valueOf(resultado).replace(".", ",")); // Mostramos el resultado en pantalla
            pantallaAlmacenada.setText(""); // Limpiamos la pantalla de operaciones
            operacionActual = ""; // Restablecemos la operación
            nuevoNumero = true; // Permitimos ingresar un nuevo número
        } catch (Exception e) {
            pantallaActual.setText("Error");
        }
    }

    private void reiniciar() {
        pantallaActual.setText("0");
        pantallaAlmacenada.setText("");
        operacionActual = "";
        resultado = 0;
        nuevoNumero = true;
    }

    private void verificarNegativo() {
        if (pantallaActual.getText().startsWith("-")) {
            pantallaActual.setForeground(Color.RED);
        } else {
            pantallaActual.setForeground(Color.WHITE);
        }
    }    

    private void cambiarModo() {
        if (modo.equals("Ambos")) modo = "Ratón";
        else if (modo.equals("Ratón")) modo = "Teclado";
        else modo = "Ambos";
        modoEntrada.setText("Modo: " + modo);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculadora::new);
    }
}
