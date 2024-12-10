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

    public Calculadora(String alumno) {
        setTitle("Calculadora - " + alumno);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración inicial de la ventana
        ajustarPantallaNormal();

        // Configurar atajos de teclado para numpad y retroceso
        configurarAtajosDeTeclado();

        // Configuración del Layout principal
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Panel de pantallas
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        add(panelPantallas, gbc);

        // Panel de botones numéricos
        JPanel panelBotones = new JPanel(new GridLayout(4, 4, 5, 5));
        panelBotones.setBackground(new Color(240, 240, 240));
        for (int i = 1; i <= 9; i++) {
            agregarBotonNumerico(panelBotones, String.valueOf(i));
        }
        agregarBotonNumerico(panelBotones, "0");
        agregarBotonNumerico(panelBotones, ",");

        // Botón "Cambiar Modo" dentro del panel de botones
        JButton botonModo = new JButton("Cambiar Modo");
        botonModo.setBackground(new Color(255, 204, 153));
        botonModo.setFont(new Font("Arial", Font.PLAIN, 14));
        botonModo.addActionListener(e -> cambiarModo());
        panelBotones.add(botonModo);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        add(panelBotones, gbc);

        // Panel de operaciones
        JPanel panelOperaciones = new JPanel(new GridLayout(3, 2, 5, 5));
        panelOperaciones.setBackground(new Color(220, 220, 220));
        agregarBotonOperacion(panelOperaciones, "+");
        agregarBotonOperacion(panelOperaciones, "-");
        agregarBotonOperacion(panelOperaciones, "*");
        agregarBotonOperacion(panelOperaciones, "/");
        agregarBotonEspecial(panelOperaciones, "C", e -> reiniciar());
        agregarBotonEspecial(panelOperaciones, "=", e -> calcularResultado());

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        add(panelOperaciones, gbc);

        // Panel de modo de entrada
        JPanel panelModo = new JPanel();
        panelModo.setBackground(Color.DARK_GRAY);
        modoEntrada = new JLabel("Modo: Ambos");
        modoEntrada.setFont(new Font("Arial", Font.ITALIC, 14));
        modoEntrada.setForeground(Color.WHITE);
        panelModo.add(modoEntrada);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        add(panelModo, gbc);

        setVisible(true);
    }

    private void ajustarPantallaNormal() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.5), 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void configurarAtajosDeTeclado() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // Números del teclado numérico
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

        // Punto decimal del teclado numérico
        inputMap.put(KeyStroke.getKeyStroke("DECIMAL"), "decimal");
        actionMap.put("decimal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modo.equals("Ambos") || modo.equals("Teclado")) {
                    ingresarNumero(",");
                }
            }
        });

        // Retroceso vinculado a "C"
        inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "borrarTodo");
        actionMap.put("borrarTodo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciar();
            }
        });

        // Operaciones del teclado
        inputMap.put(KeyStroke.getKeyStroke("ADD"), "suma");
        inputMap.put(KeyStroke.getKeyStroke("SUBTRACT"), "resta");
        inputMap.put(KeyStroke.getKeyStroke("MULTIPLY"), "multiplicacion");
        inputMap.put(KeyStroke.getKeyStroke("DIVIDE"), "division");

        actionMap.put("suma", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modo.equals("Ambos") || modo.equals("Teclado")) {
                    setOperacion("+");
                }
            }
        });

        actionMap.put("resta", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modo.equals("Ambos") || modo.equals("Teclado")) {
                    setOperacion("-");
                }
            }
        });

        actionMap.put("multiplicacion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modo.equals("Ambos") || modo.equals("Teclado")) {
                    setOperacion("*");
                }
            }
        });

        actionMap.put("division", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modo.equals("Ambos") || modo.equals("Teclado")) {
                    setOperacion("/");
                }
            }
        });

        // Enter para calcular el resultado
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

    private void cambiarModo() {
        // Cambiar el modo de entrada
        if (modo.equals("Ambos")) {
            modo = "Ratón";
        } else if (modo.equals("Ratón")) {
            modo = "Teclado";
        } else {
            modo = "Ambos";
        }
        modoEntrada.setText("Modo: " + modo);
    }

    private void agregarBotonNumerico(JPanel panel, String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Consolas", Font.BOLD, 18));
        boton.setBackground(new Color(230, 230, 230));
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        boton.addActionListener(e -> ingresarNumero(texto));
        panel.add(boton);
    }

    private void agregarBotonOperacion(JPanel panel, String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Consolas", Font.BOLD, 18));
        boton.setBackground(new Color(255, 204, 153));
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        boton.addActionListener(e -> setOperacion(texto));
        panel.add(boton);
    }

    private void agregarBotonEspecial(JPanel panel, String texto, ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Consolas", Font.BOLD, 18));
        boton.setBackground(new Color(255, 102, 102));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        boton.addActionListener(action);
        panel.add(boton);
    }

    private void ingresarNumero(String texto) {
        if (nuevoNumero) {
            pantallaActual.setText(texto.equals(",") ? "0," : texto);
            nuevoNumero = false;
        } else {
            pantallaActual.setText(pantallaActual.getText() + texto);
        }
        verificarNegativo();  // Verificamos si el número es negativo
    }

    private void setOperacion(String operacion) {
        try {
            resultado = Double.parseDouble(pantallaActual.getText().replace(",", "."));
            operacionActual = operacion;
            pantallaAlmacenada.setText(pantallaActual.getText() + " " + operacion);
            nuevoNumero = true;
            verificarNegativo();  // Verificamos si el número es negativo después de setear la operación
        } catch (Exception e) {
            pantallaActual.setText("Error");
        }
    }

    private void calcularResultado() {
        try {
            double valorActual = Double.parseDouble(pantallaActual.getText().replace(",", "."));
            switch (operacionActual) {
                case "+" -> resultado += valorActual;
                case "-" -> resultado -= valorActual;
                case "*" -> resultado *= valorActual;
                case "/" -> {
                    if (valorActual == 0) {
                        pantallaActual.setText("Error");
                        return;
                    }
                    resultado /= valorActual;
                }
            }
            pantallaActual.setText(String.valueOf(resultado).replace(".", ","));
            pantallaAlmacenada.setText("");
            operacionActual = "";
            nuevoNumero = true;
            verificarNegativo();  // Verificamos si el resultado final es negativo
        } catch (Exception ex) {
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
        // Cambiar el color a rojo si el número es negativo
        try {
            if (Double.parseDouble(pantallaActual.getText().replace(",", ".")) < 0) {
                pantallaActual.setForeground(Color.RED);
            } else {
                pantallaActual.setForeground(Color.WHITE);
            }
        } catch (NumberFormatException e) {
            // Si no es un número válido (por ejemplo "Error"), no hacemos nada
        }
    }

    public static void main(String[] args) {
        new Calculadora("David Torres");
    }
}
