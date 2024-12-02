package com.ufps.services;

import org.springframework.stereotype.Service;

import com.ufps.entities.*;
import com.ufps.repositories.CompraRepository;
import com.ufps.repositories.ProductoRepository;

import DTOs.FacturaRequest;
import jakarta.transaction.Transactional;

@Service
public class FacturaService {

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    // Inyecta más repositorios según sea necesario

    public FacturaService(CompraRepository compraRepository, ProductoRepository productoRepository) {
        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public void crearFactura(String tiendaId, FacturaRequest request) {
        // 1. Verificar cliente, tienda, vendedor, y cajero
        Cliente cliente = obtenerORegistrarCliente(request.getCliente());
        Tienda tienda = obtenerTienda(tiendaId);
        Vendedor vendedor = obtenerVendedor(request.getVendedor().getDocumento());
        Cajero cajero = validarCajero(request.getCajero().getToken());

        // 2. Crear la Compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setTienda(tienda);
        compra.setVendedor(vendedor);
        compra.setCajero(cajero);
        compra.setImpuesto(request.getImpuesto());
        compra.setFecha(LocalDateTime.now());

        compraRepository.save(compra);

        // 3. Registrar Detalles de la Compra
        for (ProductoCompraDTO prod : request.getProductos()) {
            Producto producto = productoRepository.findByReferencia(prod.getReferencia())
                               .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(prod.getCantidad());
            detalle.setDescuento(prod.getDescuento());
            detalle.setValor((producto.getPrecio() - prod.getDescuento()) * prod.getCantidad());

            // Guardar el detalle
        }

        // 4. Registrar los Pagos asociados
        // Implementa lógica similar para guardar la tabla `pago`.

    }
}