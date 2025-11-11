package com.ejemplo.conexionmysql;

import java.sql.*;
import java.util.Scanner;

public class GestionDatos {

    // Datos de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/boutiqueBrigton";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "123456";

    public static void main(String[] args) {
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("✅ Conectado a la base de datos boutiqueBrigton");

            int opcion;
            do {
                System.out.println("\n========= MENÚ PRINCIPAL =========");
                System.out.println("1. Insertar Cliente");
                System.out.println("2. Insertar Producto");
                System.out.println("3. Listar Clientes");
                System.out.println("4. Actualizar Estado de Pedido");
                System.out.println("5. Eliminar Cliente");
                System.out.println("6. Ver Ventas por Categoría (SP)");
                System.out.println("7. Registrar Pedido (SP)");
                System.out.println("8. Actualizar Stock (SP)");
                System.out.println("9. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = sc.nextInt();
                sc.nextLine(); // limpiar buffer

                switch (opcion) {
                    case 1 -> insertarCliente(conexion, sc);
                    case 2 -> insertarProducto(conexion, sc);
                    case 3 -> listarClientes(conexion);
                    case 4 -> actualizarEstadoPedido(conexion, sc);
                    case 5 -> eliminarCliente(conexion, sc);
                    case 6 -> ejecutarVentasPorCategoria(conexion);
                    case 7 -> registrarPedido(conexion, sc);
                    case 8 -> actualizarStock(conexion, sc);
                    case 9 -> System.out.println("👋 Saliendo del sistema...");
                    default -> System.out.println("❌ Opción inválida.");
                }

            } while (opcion != 9);

        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
        }
    }

    // ==============================
    // MÉTODOS CRUD Y SP
    // ==============================

    private static void insertarCliente(Connection conexion, Scanner sc) throws SQLException {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Teléfono: ");
        String telefono = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();

        String sql = "INSERT INTO Cliente (nombre, telefono, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, email);
            ps.executeUpdate();
            System.out.println("✅ Cliente insertado correctamente.");
        }
    }

    private static void insertarProducto(Connection conexion, Scanner sc) throws SQLException {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Descripción: ");
        String descripcion = sc.nextLine();
        System.out.print("Precio: ");
        double precio = sc.nextDouble();
        sc.nextLine();
        System.out.print("Categoría: ");
        String categoria = sc.nextLine();
        System.out.print("Imagen: ");
        String imagen = sc.nextLine();

        String sql = "INSERT INTO Producto (nombre, descripcion, precio, categoria, imagen) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precio);
            ps.setString(4, categoria);
            ps.setString(5, imagen);
            ps.executeUpdate();
            System.out.println("✅ Producto insertado correctamente.");
        }
    }

    private static void listarClientes(Connection conexion) throws SQLException {
        String sql = "SELECT cliente_id, nombre, telefono, email FROM Cliente ORDER BY cliente_id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n📋 LISTA DE CLIENTES:");
            while (rs.next()) {
                System.out.printf("ID: %d | %s | %s | %s%n",
                        rs.getInt("cliente_id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email"));
            }
        }
    }

    private static void actualizarEstadoPedido(Connection conexion, Scanner sc) throws SQLException {
        System.out.print("ID del Pedido: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Nuevo Estado (CREADO/CONFIRMADO/CERRADA): ");
        String estado = sc.nextLine();

        String sql = "UPDATE Pedido SET estado = ? WHERE pedido_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            int filas = ps.executeUpdate();
            if (filas > 0)
                System.out.println("✅ Estado actualizado correctamente.");
            else
                System.out.println("⚠️ Pedido no encontrado.");
        }
    }

    private static void eliminarCliente(Connection conexion, Scanner sc) throws SQLException {
        System.out.print("ID del cliente a eliminar: ");
        int id = sc.nextInt();

        String sql = "DELETE FROM Cliente WHERE cliente_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0)
                System.out.println("🗑️ Cliente eliminado correctamente.");
            else
                System.out.println("⚠️ Cliente no encontrado.");
        }
    }

    // ========= PROCEDIMIENTOS ALMACENADOS =========

    private static void ejecutarVentasPorCategoria(Connection conexion) throws SQLException {
        String sql = "{CALL VentasPorCategoria()}";
        try (CallableStatement cs = conexion.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            System.out.println("\n💰 TOTAL VENTAS POR CATEGORÍA:");
            while (rs.next()) {
                System.out.printf("%s → $%.2f%n",
                        rs.getString("categoria"),
                        rs.getDouble("total_vendido"));
            }
        }
    }

    private static void registrarPedido(Connection conexion, Scanner sc) throws SQLException {
        System.out.print("ID del cliente: ");
        int clienteId = sc.nextInt();
        System.out.print("Total del pedido: ");
        double total = sc.nextDouble();
        sc.nextLine();
        System.out.print("Estado: ");
        String estado = sc.nextLine();
        System.out.print("Canal: ");
        String canal = sc.nextLine();

        String sql = "{CALL RegistrarPedido(?, ?, ?, ?)}";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, clienteId);
            cs.setDouble(2, total);
            cs.setString(3, estado);
            cs.setString(4, canal);
            cs.execute();
            System.out.println("✅ Pedido registrado exitosamente.");
        }
    }

    private static void actualizarStock(Connection conexion, Scanner sc) throws SQLException {
        System.out.print("ID del producto: ");
        int productoId = sc.nextInt();
        sc.nextLine();
        System.out.print("Talla (puede ser NULL): ");
        String talla = sc.nextLine();
        System.out.print("Cantidad vendida: ");
        int cantidad = sc.nextInt();

        String sql = "{CALL ActualizarStock(?, ?, ?)}";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, productoId);
            if (talla.isBlank())
                cs.setNull(2, Types.VARCHAR);
            else
                cs.setString(2, talla);
            cs.setInt(3, cantidad);
            cs.execute();
            System.out.println("✅ Stock actualizado correctamente.");
        }
    }
}
