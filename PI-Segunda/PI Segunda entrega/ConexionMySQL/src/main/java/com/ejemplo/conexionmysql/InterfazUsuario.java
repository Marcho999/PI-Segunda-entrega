package com.ejemplo.conexionmysql;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.sql.*;

public class InterfazUsuario extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/boutiqueBrigton";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "123456";

    private Connection conexion;
    private DefaultTableModel modeloCatalogo;
    private String nombreUsuario;

    public InterfazUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;

        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Boutique Brigton - Catálogo de Usuario");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        listarCatalogo();
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(250, 240, 230));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ===== ENCABEZADO =====
        JLabel lblBienvenida = new JLabel("Bienvenido, " + nombreUsuario + " 👋", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblBienvenida.setBorder(new EmptyBorder(10, 10, 20, 10));
        panelPrincipal.add(lblBienvenida, BorderLayout.NORTH);

        // ===== PANEL SUPERIOR CON BOTÓN WHATSAPP =====
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelSuperior.setBackground(new Color(250, 240, 230));

        JButton btnWhatsApp = new JButton("Contactar Asesor por WhatsApp");
        btnWhatsApp.setBackground(new Color(37, 211, 102));
        btnWhatsApp.setForeground(Color.WHITE);
        btnWhatsApp.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnWhatsApp.setFocusPainted(false);
        btnWhatsApp.addActionListener(e -> conectarWhatsApp());

        panelSuperior.add(btnWhatsApp);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ===== TABLA DE CATÁLOGO =====
        modeloCatalogo = new DefaultTableModel(new String[]{"Producto", "Talla", "Stock", "Precio Unitario"}, 0);
        JTable tablaCatalogo = new JTable(modeloCatalogo);
        tablaCatalogo.setRowHeight(30);
        tablaCatalogo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tablaCatalogo.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(tablaCatalogo);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Catálogo de Productos Disponibles"));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // ===== BOTÓN DE ACTUALIZAR =====
        JButton btnActualizar = new JButton("Actualizar Catálogo");
        btnActualizar.setBackground(new Color(135, 206, 250));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> listarCatalogo());

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(250, 240, 230));
        panelBoton.add(btnActualizar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void listarCatalogo() {
        modeloCatalogo.setRowCount(0);
        String sql = "SELECT p.nombre, i.talla, i.stock, i.precio_unitario " +
                     "FROM Inventario i JOIN Producto p ON i.producto_id = p.idproducto ORDER BY p.nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloCatalogo.addRow(new Object[]{
                        rs.getString("nombre"),
                        rs.getString("talla"),
                        rs.getInt("stock"),
                        rs.getDouble("precio_unitario")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar catálogo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void conectarWhatsApp() {
        try {
            String numero = "3170650834"; // Cambia por tu número de WhatsApp
            String mensaje = "Hola, necesito asesoramiento en Boutique Brigton";
            String url = "https://wa.me/" + numero + "?text=" + java.net.URLEncoder.encode(mensaje, "UTF-8");
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir WhatsApp: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazUsuario("Cliente").setVisible(true));
    }
}
