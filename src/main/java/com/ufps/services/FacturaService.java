package com.ufps.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ufps.entities.*;
import com.ufps.repositories.*;

import DTOs.*;
import jakarta.transaction.Transactional;
import lombok.*;

@Service
@Data
@RequiredArgsConstructor
@Transactional
public class FacturaService {

    private final TiendaRepository tiendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final VendedorRepository vendedorRepository;
    private final CajeroRepository cajeroRepository;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final PagoRepository pagoRepository;

    public void procesarFactura(String tiendaUuid, FacturaRequest facturaRequest) {
        // Buscar Tienda
        Tienda tienda = tiendaRepository.findByUuid(tiendaUuid)
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada con UUID: " + tiendaUuid));

        // Buscar o Crear Cliente
        Cliente cliente = clienteRepository.findByDocumento(facturaRequest.getCliente().getDocumento())
                .orElseGet(() -> {
                    Cliente nuevoCliente = new Cliente();
                    nuevoCliente.setNombre(facturaRequest.getCliente().getNombre());
                    nuevoCliente.setDocumento(facturaRequest.getCliente().getDocumento());

                    TipoDocumento tipoDocumento = new TipoDocumento();
                    tipoDocumento.setId(facturaRequest.getCliente().getTipoDocumentoId());
                    nuevoCliente.setTipoDocumento(tipoDocumento);
                    
                    return clienteRepository.save(nuevoCliente);
                });

        // Buscar Vendedor
        Vendedor vendedor = vendedorRepository.findByDocumento(facturaRequest.getVendedor().getDocumento())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con documento: " + facturaRequest.getVendedor().getDocumento()));

        // Buscar Cajero
        Cajero cajero = cajeroRepository.findByToken(facturaRequest.getCajero().getToken())
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado con token: " + facturaRequest.getCajero().getToken()));

        // Crear y Guardar la Compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setTienda(tienda);
        compra.setVendedor(vendedor);
        compra.setCajero(cajero);
        compra.setFecha(LocalDateTime.now());
        compra.setImpuestos(facturaRequest.getImpuestos());
        compra = compraRepository.save(compra);

        // Procesar Detalles de Compra y Calcular Total
        double total = 0.0;
        for (DetalleProductoDTO detalleProducto : facturaRequest.getProductos()) {
            Producto producto = productoRepository.findByReferencia(detalleProducto.getReferencia())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con referencia: " + detalleProducto.getReferencia()));

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleProducto.getCantidad());

            double precioFinal = producto.getPrecio() * detalleProducto.getCantidad() * 
                                 (1 - detalleProducto.getDescuento() / 100.0);
            detalle.setPrecio(precioFinal);
            detalle.setDescuento(detalleProducto.getDescuento());

            detalleCompraRepository.save(detalle);
            total += precioFinal;
        }
        compra.setTotal(total);
        compraRepository.save(compra);

        // Procesar Pagos
        for (MedioPagoDTO medioPago : facturaRequest.getMediosPago()) {
            TipoPago tipoPago = tipoPagoRepository.findByNombre(medioPago.getTipoPago())
                    .orElseThrow(() -> new RuntimeException("Tipo de pago no encontrado: " + medioPago.getTipoPago()));

            Pago pago = new Pago();
            pago.setCompra(compra);
            pago.setTipoPago(tipoPago);
            pago.setTarjetaTipo(medioPago.getTipoTarjeta());
            pago.setCuotas(medioPago.getCuotas());
            pago.setValor(medioPago.getValor());

            pagoRepository.save(pago);
        }
    }
}
