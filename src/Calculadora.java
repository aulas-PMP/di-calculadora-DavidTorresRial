import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculadora extends JFrame {
    private JTextField pantallaActual, pantallaAlmacenada;
    private String operacionActual = "";
    private double resultado = 0;
    private boolean nuevoNumero = true;
    private JLabel modoEntrada;

    public Calculadora(String alumno) {
        setTitle("Calculadora - " + alumno);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración inicial de la ventana
        ajustarPantallaNormal();

        // Agregar KeyListener para permitir usar el teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char tecla = e.getKeyChar();
                if (Character.isDigit(tecla)) {
                    ingresarNumero(String.valueOf(tecla));
                } else if (tecla == '+') {
                    setOperacion("+");
                } else if (tecla == '-') {
                    setOperacion("-");
                } else if (tecla == '*') {
                    setOperacion("*");
                } else if (tecla == '/') {
                    setOperacion("/");
                } else if (tecla == '=') {
                    calcularResultado();
                } else if (tecla == KeyEvent.VK_BACK_SPACE) {
                    reiniciar();
                }
            }
        });
        setFocusable(true);

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
        JPanel panelBotones = new JPanel(new GridLayout(4, 3, 5, 5));
        panelBotones.setBackground(new Color(240, 240, 240));
        for (int i = 1; i <= 9; i++) {
            agregarBotonNumerico(panelBotones, String.valueOf(i));
        }
        agregarBotonNumerico(panelBotones, "0");
        agregarBotonNumerico(panelBotones, ",");

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
        modoEntrada = new JLabel("Modo: Libre");
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
        setResizable(true); // Permite redimensionar, pero el botón de maximizar debería estar disponible
    }

    private void ajustarPantallaMaximizada() {
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza la ventana
        setResizable(true); // Permite redimensionar cuando la ventana esté maximizada
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

    private void setModo(String modo) {
        modoEntrada.setText("Modo: " + modo);
    }

    private void ingresarNumero(String texto) {
        if (nuevoNumero) {
            pantallaActual.setText(texto.equals(",") ? "0," : texto);
            nuevoNumero = false;
        } else {
            pantallaActual.setText(pantallaActual.getText() + texto);
        }
    }

    private void setOperacion(String operacion) {
        String actual = pantallaActual.getText();

        // Si la pantalla actual termina en un operador, reemplazamos el último operador con el nuevo
        if (actual.length() > 0 && "+-*/".contains(String.valueOf(actual.charAt(actual.length() - 1)))) {
            pantallaActual.setText(actual.substring(0, actual.length() - 1) + operacion);
        } else if (actual.length() > 0) {
            if (operacionActual.isEmpty()) {
                resultado = Double.parseDouble(actual.replace(",", "."));
            }
            operacionActual = operacion;
            pantallaAlmacenada.setText(pantallaActual.getText() + " " + operacion);
            nuevoNumero = true;
        }
    }

    private void calcularResultado() {
        try {
            double valorActual = Double.parseDouble(pantallaActual.getText().replace(",", "."));
            if (operacionActual.isEmpty()) {
                return;  // Si no hay operación, no calculamos
            }
            switch (operacionActual) {
                case "+":
                    resultado += valorActual;
                    break;
                case "-":
                    resultado -= valorActual;
                    break;
                case "*":
                    resultado *= valorActual;
                    break;
                case "/":
                    if (valorActual == 0) {
                        throw new ArithmeticException("División por cero");
                    }
                    resultado /= valorActual;
                    break;
            }
            pantallaActual.setText(String.valueOf(resultado).replace(".", ","));
            pantallaAlmacenada.setText("");
            operacionActual = "";
            nuevoNumero = true;
        } catch (Exception ex) {
            pantallaActual.setText("Error");
        }
    }

    private void reiniciar() {
        pantallaActual.setText("0");
        pantallaAlmacenada.setText("");
        resultado = 0;
        operacionActual = "";
        nuevoNumero = true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculadora("David"));
    }
}
