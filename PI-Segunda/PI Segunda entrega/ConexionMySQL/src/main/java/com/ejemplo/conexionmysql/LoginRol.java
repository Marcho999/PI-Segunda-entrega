package com.ejemplo.conexionmysql;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginRol extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/boutiqueBrigton";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "123456";

    public LoginRol() {
        setTitle("Boutique Brigton - Selección de Rol");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 245, 230));

        JLabel lblTitulo = new JLabel("Selecciona tu Rol", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        panelBotones.setBackground(new Color(255, 245, 230));

        JButton btnUsuario = new JButton("Entrar como Usuario");
        btnUsuario.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnUsuario.setBackground(new Color(135, 206, 235));
        btnUsuario.setForeground(Color.WHITE);
        btnUsuario.setFocusPainted(false);
        btnUsuario.addActionListener(e -> registroUsuario());

        JButton btnAdmin = new JButton("Entrar como Administrador");
        btnAdmin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAdmin.setBackground(new Color(255, 160, 122));
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.setFocusPainted(false);
        btnAdmin.addActionListener(e -> {
            new TiendaBoutiqueCompleta().setVisible(true);
            dispose();
        });

        panelBotones.add(btnUsuario);
        panelBotones.add(btnAdmin);
        panel.add(panelBotones, BorderLayout.CENTER);

        add(panel);
    }

    private void registroUsuario() {
        JTextField nombreField = new JTextField();
        JTextField telefonoField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] campos = {
            "Nombre:", nombreField,
            "Teléfono:", telefonoField,
            "Email:", emailField
        };

        int opcion = JOptionPane.showConfirmDialog(this, campos, "Registro de Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String email = emailField.getText().trim();

            if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA)) {
                String sql = "INSERT INTO Cliente (nombre, telefono, email) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, telefono);
                    ps.setString(3, email);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Registro exitoso. Bienvenido " + nombre + "!");
                new InterfazUsuario(nombre).setVisible(true);
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginRol().setVisible(true));
    }
}
