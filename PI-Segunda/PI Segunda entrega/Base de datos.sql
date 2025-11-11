CREATE DATABASE boutiqueBrigton;
USE boutiqueBrigton;

CREATE TABLE Usuario (
    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE ChatWhatsApp (
    atencion_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    asesor VARCHAR(100),
    telefono VARCHAR(20)
);

-- Tabla Cliente
CREATE TABLE Cliente (
    cliente_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE Producto (
    idproducto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT,
    precio DOUBLE,
    categoria VARCHAR(50),
    imagen VARCHAR(100)
);

CREATE TABLE Pedido (
    pedido_id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT,
    fecha DATE,
    estado VARCHAR(20),
    canal VARCHAR(20),
    total DOUBLE,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(cliente_id)
);

CREATE TABLE DetallePedido (
    detalle_id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT,
    producto_id INT,
    stock INT,
    talla VARCHAR(10),
    precio_unitario DOUBLE,
    FOREIGN KEY (pedido_id) REFERENCES Pedido(pedido_id),
    FOREIGN KEY (producto_id) REFERENCES Producto(idproducto)
);

CREATE TABLE Inventario (
    inventario_id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT,
    stock INT,
    talla VARCHAR(10),
    precio_unitario DOUBLE,
    fecha DATE,
    FOREIGN KEY (producto_id) REFERENCES Producto(idproducto)
);


INSERT INTO Usuario (nombre, telefono, email) 
VALUES ('Marvin Santiago', '3170684625', 'MarchoGOAT@gang.com');


INSERT INTO Cliente (nombre, telefono, email) VALUES
('Laura Torres', '3001112233', 'laura@gmail.com'),
('Juan Gómez', '3012223344', 'juan@hotmail.com'),
('Sofía Herrera', '3023334455', 'sofia@yahoo.com');


INSERT INTO Producto (nombre, descripcion, precio, categoria, imagen) VALUES
('Nike 200', 'Cómodas y con estilo', 25.50, 'Zapatos', 'Nike.jpg'),
('Camisa Polo', 'Camisa deportiva única', 18.99, 'Camisas', 'POLO.jpg'),
('Pantaloneta', 'Pantaloneta cómoda para el hogar', 12.00, 'Pantalonetas', 'Pantaloneta.jpg');


INSERT INTO Pedido (cliente_id, fecha, estado, canal, total) VALUES
(1, '2025-09-15', 'CONFIRMADO', 'Web', 80.50),
(2, '2025-09-16', 'CREADO', 'Tienda', 45.00),
(3, '2025-09-17', 'CERRADA', 'Web', 120.00);


INSERT INTO DetallePedido (pedido_id, producto_id, stock, talla, precio_unitario) VALUES
(1, 1, 1, 'M', 25.50),
(1, 2, 2, NULL, 18.99),
(2, 3, 3, NULL, 15.00);


INSERT INTO Inventario (producto_id, stock, talla, precio_unitario, fecha) VALUES
(1, 15, 'M', 22.00, '2025-09-01'),
(2, 30, NULL, 17.50, '2025-09-02'),
(3, 50, NULL, 10.00, '2025-09-03');


INSERT INTO ChatWhatsApp (nombre, asesor, telefono) VALUES
('Consulta 1', 'Marvin Santiago', '3170684523'),
('Seguimiento 2', 'Luis Gómez', '3205556677');

-- Querys

--  Pedidos con nombre del cliente y total de productos
SELECT 
    p.pedido_id,
    c.nombre AS cliente,
    COUNT(dp.detalle_id) AS cantidad_productos,
    p.total,
    p.estado,
    p.fecha
FROM Pedido p
JOIN Cliente c ON p.cliente_id = c.cliente_id
JOIN DetallePedido dp ON p.pedido_id = dp.pedido_id
GROUP BY p.pedido_id, c.nombre, p.total, p.estado, p.fecha
ORDER BY p.fecha DESC;

--  Total vendido por categoría
SELECT 
    pr.categoria,
    SUM(dp.stock * dp.precio_unitario) AS total_vendido
FROM DetallePedido dp
JOIN Producto pr ON dp.producto_id = pr.idproducto
GROUP BY pr.categoria
ORDER BY total_vendido DESC;

--  Valor total del inventario
SELECT 
    p.nombre,
    i.talla,
    i.stock,
    i.precio_unitario,
    (i.stock * i.precio_unitario) AS valor_total,
    i.fecha
FROM Inventario i
JOIN Producto p ON i.producto_id = p.idproducto
ORDER BY valor_total DESC;

--  Clientes con pedidos confirmados o cerrados
SELECT DISTINCT c.nombre, c.telefono, c.email
FROM Cliente c
JOIN Pedido p ON c.cliente_id = p.cliente_id
WHERE p.estado IN ('CONFIRMADO', 'CERRADA');

--  Clientes con compras mayores a 100
SELECT nombre, telefono, email
FROM Cliente
WHERE cliente_id IN (
    SELECT cliente_id
    FROM Pedido
    GROUP BY cliente_id
    HAVING SUM(total) > 100
);

--  Detalle completo de pedidos
SELECT 
    c.nombre AS cliente,
    p.pedido_id,
    pr.nombre AS producto,
    dp.stock AS cantidad,
    dp.precio_unitario,
    (dp.stock * dp.precio_unitario) AS subtotal,
    p.total AS total_pedido
FROM DetallePedido dp
JOIN Pedido p ON dp.pedido_id = p.pedido_id
JOIN Cliente c ON p.cliente_id = c.cliente_id
JOIN Producto pr ON dp.producto_id = pr.idproducto
ORDER BY p.pedido_id;

--  Total de ventas por día
SELECT 
    fecha,
    SUM(total) AS total_vendido
FROM Pedido
GROUP BY fecha
ORDER BY fecha DESC;

--   actualización de inventario
UPDATE Inventario
SET stock = stock - 2
WHERE producto_id = 1 AND talla = 'M';

--  Productos con precio mayor al promedio
SELECT nombre, precio
FROM Producto
WHERE precio > (SELECT AVG(precio) FROM Producto);

--  Producto más vendido
SELECT 
    pr.nombre,
    SUM(dp.stock) AS total_vendido
FROM DetallePedido dp
JOIN Producto pr ON dp.producto_id = pr.idproducto
GROUP BY pr.nombre
ORDER BY total_vendido DESC
LIMIT 1;