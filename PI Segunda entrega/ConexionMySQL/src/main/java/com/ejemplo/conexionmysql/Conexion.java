package com.ejemplo.conexionmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    public static void main(String[] args) {
        // Cambia los valores por los de tu entorno
        String url = "jdbc:mysql://localhost:3306/boutiqueBrigton";
        String usuario = "root";
        String contraseña = "123456";

        try {
            // Cargar el driver de MySQL (opcional en versiones nuevas)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Intentar conectar
            Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("✅ Conexión exitosa a la base de datos MySQL!");

            // Cerrar conexión
            conexion.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ No se encontró el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
    }
}
