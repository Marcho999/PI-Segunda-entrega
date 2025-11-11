package com.ejemplo.conexionmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertarDatos {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/boutiqueBrigton";
        String usuario = "root";
        String contraseña = "123456";

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("✅ Conectado a la base de datos.");

            System.out.print("Ingrese el nombre: ");
            String nombre = sc.nextLine();

            System.out.print("Ingrese el email: ");
            String email = sc.nextLine();

            System.out.print("Ingrese el teléfono: ");
            String telefono = sc.nextLine();

            String sql = "INSERT INTO cliente (nombre, email, telefono) VALUES (?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, telefono);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("✅ Registro insertado correctamente.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar datos: " + e.getMessage());
        }
    }
}
