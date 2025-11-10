package com.ejemplo.conexionmysql;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class TiendaBoutiqueCompleta extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/boutiqueBrigton";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "123456";

    private Connection conexion;

    // Tablas y modelos
    private DefaultTableModel modeloClientes, modeloProductos, modeloPedidos;
    private DefaultTableModel modeloInventario, modeloClientesDestacados;

    private JPanel panelGraficoVentas, panelGraficoProductos;

    public TiendaBoutiqueCompleta() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Boutique Brigton - Sistema Completo");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        // ======== CLIENTES ========
        JPanel panelClientes = crearPanel(Color.decode("#FFF5F5"));
        modeloClientes = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Email"}, 0);
        JTable tablaClientes = crearTabla(modeloClientes);
        panelClientes.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        JPanel panelClientesBotones = new JPanel();
        JButton btnInsertarCliente = crearBoton("Insertar Cliente", "#FF8C69");
        btnInsertarCliente.addActionListener(e -> insertarCliente());
        JButton btnEliminarCliente = crearBoton("Eliminar Cliente", "#CD5C5C");
        btnEliminarCliente.addActionListener(e -> eliminarCliente(tablaClientes));
        JButton btnActualizarClientes = crearBoton("Actualizar Clientes", "#FF8C69");
        btnActualizarClientes.addActionListener(e -> listarClientes());

        panelClientesBotones.add(btnInsertarCliente);
        panelClientesBotones.add(btnEliminarCliente);
        panelClientesBotones.add(btnActualizarClientes);
        panelClientes.add(panelClientesBotones, BorderLayout.SOUTH);

        tabbedPane.addTab("Clientes", panelClientes);

        // ======== PRODUCTOS ========
        JPanel panelProductos = crearPanel(Color.decode("#F5F5FF"));
        modeloProductos = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Precio", "Categoría"}, 0);
        JTable tablaProductos = crearTabla(modeloProductos);
        panelProductos.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel panelProductosBotones = new JPanel();
        JButton btnInsertarProducto = crearBoton("Insertar Producto", "#6495ED");
        btnInsertarProducto.addActionListener(e -> insertarProducto());
        JButton btnActualizarProductos = crearBoton("Actualizar Productos", "#6495ED");
        btnActualizarProductos.addActionListener(e -> listarProductos());
        panelProductosBotones.add(btnInsertarProducto);
        panelProductosBotones.add(btnActualizarProductos);
        panelProductos.add(panelProductosBotones, BorderLayout.SOUTH);

        tabbedPane.addTab("Productos", panelProductos);

        // ======== PEDIDOS ========
        JPanel panelPedidos = crearPanel(Color.decode("#F0FFF0"));
        modeloPedidos = new DefaultTableModel(new String[]{"ID", "Cliente", "Total", "Estado", "Canal"}, 0);
        JTable tablaPedidos = crearTabla(modeloPedidos);
        panelPedidos.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        JPanel panelPedidosBotones = new JPanel();
        JButton btnRegistrarPedido = crearBoton("Registrar Pedido", "#66CDAA");
        btnRegistrarPedido.addActionListener(e -> registrarPedido());
        JButton btnActualizarPedidos = crearBoton("Actualizar Pedidos", "#66CDAA");
        btnActualizarPedidos.addActionListener(e -> listarPedidos());
        panelPedidosBotones.add(btnRegistrarPedido);
        panelPedidosBotones.add(btnActualizarPedidos);
        panelPedidos.add(panelPedidosBotones, BorderLayout.SOUTH);

        tabbedPane.addTab("Pedidos", panelPedidos);

        // ======== DASHBOARD ========
        JPanel panelDashboard = new JPanel();
        panelDashboard.setLayout(new GridLayout(2, 2, 15, 15));
        panelDashboard.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelDashboard.setBackground(new Color(255, 245, 225));

        // Panel gráfico ventas por categoría
        panelGraficoVentas = new JPanel(new BorderLayout());
        panelGraficoVentas.setBorder(BorderFactory.createTitledBorder("Ventas por Categoría"));
        panelDashboard.add(panelGraficoVentas);

        // Panel gráfico productos más vendidos
        panelGraficoProductos = new JPanel(new BorderLayout());
        panelGraficoProductos.setBorder(BorderFactory.createTitledBorder("Productos Más Vendidos"));
        panelDashboard.add(panelGraficoProductos);

        // Panel inventario
        modeloInventario = new DefaultTableModel(new String[]{"Producto", "Talla", "Stock", "Precio Unitario"}, 0);
        JTable tablaInventario = crearTabla(modeloInventario);
        JPanel panelInventario = new JPanel(new BorderLayout());
        panelInventario.setBorder(BorderFactory.createTitledBorder("Inventario Actual"));
        panelInventario.add(new JScrollPane(tablaInventario), BorderLayout.CENTER);
        panelDashboard.add(panelInventario);

        // Panel clientes destacados
        modeloClientesDestacados = new DefaultTableModel(new String[]{"Nombre", "Teléfono", "Email"}, 0);
        JTable tablaClientesDestacados = crearTabla(modeloClientesDestacados);
        JPanel panelClientesDestacados = new JPanel(new BorderLayout());
        panelClientesDestacados.setBorder(BorderFactory.createTitledBorder("Clientes Compras > 100"));
        panelClientesDestacados.add(new JScrollPane(tablaClientesDestacados), BorderLayout.CENTER);
        panelDashboard.add(panelClientesDestacados);

        JButton btnActualizarDashboard = crearBoton("Actualizar Dashboard", "#FFA07A");
        btnActualizarDashboard.addActionListener(e -> actualizarDashboard());
        panelDashboard.add(btnActualizarDashboard);

        tabbedPane.addTab("Dashboard", panelDashboard);

        add(tabbedPane);

        // Inicializar datos
        listarClientes();
        listarProductos();
        listarPedidos();
        actualizarDashboard();
    }

    // ======== UTILES DE UI ========
    private JPanel crearPanel(Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        return panel;
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(new Color(255, 218, 185));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                return c;
            }
        });
        return tabla;
    }

    private JButton crearBoton(String texto, String colorHex) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.decode(colorHex));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return btn;
    }

    // ======== FUNCIONES CRUD ========
    private void listarClientes() {
        modeloClientes.setRowCount(0);
        String sql = "SELECT * FROM Cliente ORDER BY cliente_id";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloClientes.addRow(new Object[]{
                        rs.getInt("cliente_id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            mostrarError("clientes", e);
        }
    }

    private void insertarCliente() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        String telefono = JOptionPane.showInputDialog("Teléfono:");
        String email = JOptionPane.showInputDialog("Email:");

        if (nombre != null && telefono != null && email != null) {
            String sql = "INSERT INTO Cliente (nombre, telefono, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setString(3, email);
                ps.executeUpdate();
                listarClientes();
                JOptionPane.showMessageDialog(this, "Cliente insertado correctamente.");
            } catch (SQLException e) {
                mostrarError("insertar cliente", e);
            }
        }
    }

    private void eliminarCliente(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            int id = (int) modeloClientes.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar cliente seleccionado?");
            if (confirm == JOptionPane.YES_OPTION) {
                try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM Cliente WHERE cliente_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    listarClientes();
                    JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                } catch (SQLException e) {
                    mostrarError("eliminar cliente", e);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
        }
    }

    private void listarProductos() {
        modeloProductos.setRowCount(0);
        String sql = "SELECT * FROM Producto ORDER BY idproducto";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloProductos.addRow(new Object[]{
                        rs.getInt("idproducto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getString("categoria")
                });
            }
        } catch (SQLException e) {
            mostrarError("productos", e);
        }
    }

    private void insertarProducto() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        String descripcion = JOptionPane.showInputDialog("Descripción:");
        String precioStr = JOptionPane.showInputDialog("Precio:");
        String categoria = JOptionPane.showInputDialog("Categoría:");

        if (nombre != null && descripcion != null && precioStr != null && categoria != null) {
            try {
                double precio = Double.parseDouble(precioStr);
                String sql = "INSERT INTO Producto (nombre, descripcion, precio, categoria, imagen) VALUES (?, ?, ?, ?, '')";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, descripcion);
                    ps.setDouble(3, precio);
                    ps.setString(4, categoria);
                    ps.executeUpdate();
                    listarProductos();
                    JOptionPane.showMessageDialog(this, "Producto insertado correctamente.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.");
            } catch (SQLException e) {
                mostrarError("insertar producto", e);
            }
        }
    }

    private void listarPedidos() {
        modeloPedidos.setRowCount(0);
        String sql = "SELECT p.pedido_id, c.nombre, p.total, p.estado, p.canal FROM Pedido p JOIN Cliente c ON p.cliente_id = c.cliente_id ORDER BY p.fecha DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloPedidos.addRow(new Object[]{
                        rs.getInt("pedido_id"),
                        rs.getString("nombre"),
                        rs.getDouble("total"),
                        rs.getString("estado"),
                        rs.getString("canal")
                });
            }
        } catch (SQLException e) {
            mostrarError("pedidos", e);
        }
    }

    private void registrarPedido() {
        try {
            String clienteIdStr = JOptionPane.showInputDialog("ID del cliente:");
            String totalStr = JOptionPane.showInputDialog("Total del pedido:");
            String estado = JOptionPane.showInputDialog("Estado:");
            String canal = JOptionPane.showInputDialog("Canal:");

            if (clienteIdStr != null && totalStr != null && estado != null && canal != null) {
                int clienteId = Integer.parseInt(clienteIdStr);
                double total = Double.parseDouble(totalStr);

                String sql = "{CALL RegistrarPedido(?, ?, ?, ?)}";
                try (CallableStatement cs = conexion.prepareCall(sql)) {
                    cs.setInt(1, clienteId);
                    cs.setDouble(2, total);
                    cs.setString(3, estado);
                    cs.setString(4, canal);
                    cs.execute();
                    listarPedidos();
                    JOptionPane.showMessageDialog(this, "Pedido registrado correctamente.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Datos numéricos inválidos.");
        } catch (SQLException e) {
            mostrarError("registrar pedido", e);
        }
    }

    private void actualizarDashboard() {
        cargarGraficoVentasPorCategoria();
        cargarGraficoProductosMasVendidos();
        listarInventario();
        listarClientesDestacados();
    }

    private void cargarGraficoVentasPorCategoria() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = "CALL VentasPorCategoria()";
        try (CallableStatement cs = conexion.prepareCall(sql); ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                dataset.addValue(rs.getDouble("total_vendido"), "Ventas", rs.getString("categoria"));
            }
        } catch (SQLException e) {
            mostrarError("ventas por categoría", e);
        }
        JFreeChart chart = ChartFactory.createBarChart("Ventas por Categoría", "Categoría", "Total Vendido", dataset);
        panelGraficoVentas.removeAll();
        panelGraficoVentas.add(new ChartPanel(chart), BorderLayout.CENTER);
        panelGraficoVentas.validate();
    }

    private void cargarGraficoProductosMasVendidos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = "SELECT pr.nombre, SUM(dp.stock) AS total_vendido FROM DetallePedido dp JOIN Producto pr ON dp.producto_id = pr.idproducto GROUP BY pr.nombre ORDER BY total_vendido DESC LIMIT 10";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                dataset.addValue(rs.getInt("total_vendido"), "Cantidad Vendida", rs.getString("nombre"));
            }
        } catch (SQLException e) {
            mostrarError("productos más vendidos", e);
        }
        JFreeChart chart = ChartFactory.createBarChart("Productos Más Vendidos", "Producto", "Cantidad Vendida", dataset);
        panelGraficoProductos.removeAll();
        panelGraficoProductos.add(new ChartPanel(chart), BorderLayout.CENTER);
        panelGraficoProductos.validate();
    }

    private void listarInventario() {
        modeloInventario.setRowCount(0);
        String sql = "SELECT p.nombre, i.talla, i.stock, i.precio_unitario FROM Inventario i JOIN Producto p ON i.producto_id = p.idproducto ORDER BY p.nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloInventario.addRow(new Object[]{
                        rs.getString("nombre"),
                        rs.getString("talla"),
                        rs.getInt("stock"),
                        rs.getDouble("precio_unitario")
                });
            }
        } catch (SQLException e) {
            mostrarError("inventario", e);
        }
    }

    private void listarClientesDestacados() {
        modeloClientesDestacados.setRowCount(0);
        String sql = "SELECT nombre, telefono, email FROM Cliente WHERE cliente_id IN (SELECT cliente_id FROM Pedido GROUP BY cliente_id HAVING SUM(total) > 100)";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloClientesDestacados.addRow(new Object[]{
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            mostrarError("clientes destacados", e);
        }
    }

    private void mostrarError(String contexto, Exception e) {
        JOptionPane.showMessageDialog(this, "Error al procesar " + contexto + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TiendaBoutiqueCompleta().setVisible(true));
    }
}
