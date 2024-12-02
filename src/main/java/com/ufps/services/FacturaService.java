package com.ufps.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufps.entities.*;
import com.ufps.repositories.*;

import DTOs.*;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private TiendaRepository tiendaRepository;

    @Autowired
    private CajeroRepository cajeroRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoPagoRepository tipoPagoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    /**
     * Método para crear una factura en el sistema.
     *
     * @param tiendaUuid UUID de la tienda donde se realiza la compra.
     * @param request    Objeto con la información de la factura.
     */
    @Transactional
    public void crearFactura(String tiendaUuid, FacturaRequest request) {
        // Validar tienda
        Optional<Tienda> tiendaOpt = tiendaRepository.findByUuid(tiendaUuid);
        if (tiendaOpt.isEmpty()) {
            throw new IllegalArgumentException("Tienda no encontrada.");
        }
        Tienda tienda = tiendaOpt.get();

        // Validar o registrar cliente
        Cliente cliente = clienteRepository.findByDocumento(request.getCliente().getDocumento());
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setDocumento(request.getCliente().getDocumento());
            cliente.setNombre(request.getCliente().getNombre());
            cliente.setTipoDocumento(tipoDocumentoRepository.findByNombre(request.getCliente().getTipoDocumento()));
            clienteRepository.save(cliente);
        }

        // Validar vendedor
        Vendedor vendedor = vendedorRepository.findByDocumento(request.getVendedor().getDocumento());
        if (vendedor == null) {
            throw new IllegalArgumentException("Vendedor no encontrado.");
        }

        // Validar cajero
        Cajero cajero = cajeroRepository.findByToken(request.getCajero().getToken());
        if (cajero == null || !cajero.getTienda().getId().equals(tienda.getId())) {
            throw new IllegalArgumentException("Cajero no autorizado.");
        }

        // Crear compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setTienda(tienda);
        compra.setVendedor(vendedor);
        compra.setCajero(cajero);
        compra.setImpuestos(request.getImpuesto());
        compra.setTotal(0.0);
        compra.setObservaciones("Factura creada");
        compraRepository.save(compra);

        // Procesar productos y calcular total
        double total = 0.0;
        for (ProductoRequest productoRequest : request.getProductos()) {
            Producto producto = productoRepository.findByReferencia(productoRequest.getReferencia());
            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + productoRequest.getReferencia());
            }

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(productoRequest.getCantidad());
            detalle.setDescuento(productoRequest.getDescuento());
            detalle.setPrecio(producto.getPrecio() * productoRequest.getCantidad() - productoRequest.getDescuento());
            detalleCompraRepository.save(detalle);

            total += detalle.getPrecio();
        }
        compra.setTotal(total + request.getImpuesto());
        compraRepository.save(compra);

        // Procesar pagos
        for (PagoRequest pagoRequest : request.getMediosPago()) {
            Pago pago = new Pago();
            pago.setCompra(compra);
            pago.setTipoPago(tipoPagoRepository.findByNombre(pagoRequest.getTipoPago()));
            pago.setTarjetaTipo(pagoRequest.getTipoTarjeta());
            pago.setCuotas(pagoRequest.getCuotas());
            pago.setValor(pagoRequest.getValor());
            pagoRepository.save(pago);
        }
    }

    /**
     * Método para consultar una factura.
     *
     * @param tiendaUuid UUID de la tienda.
     * @param request    Objeto con la información de consulta.
     * @return Objeto con los detalles de la factura.
     */
    @Transactional(readOnly = true)
    public FacturaResponse consultarFactura(String tiendaUuid, FacturaConsultaRequest request) {
        // Validar tienda
        Optional<Tienda> tiendaOpt = tiendaRepository.findByUuid(tiendaUuid);
        if (tiendaOpt.isEmpty()) {
            throw new IllegalArgumentException("Tienda no encontrada.");
        }
        Tienda tienda = tiendaOpt.get();

        // Validar cajero por token
        Cajero cajero = cajeroRepository.findByToken(request.getToken());
        if (cajero == null || !cajero.getTienda().getId().equals(tienda.getId())) {
            throw new IllegalArgumentException("Cajero no autorizado.");
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findByDocumento(request.getCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        // Validar factura
        Compra compra = compraRepository.findById(request.getFactura())
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada."));
        if (!compra.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("La factura no pertenece al cliente proporcionado.");
        }

        // Construir respuesta
        FacturaResponse response = new FacturaResponse();
        response.setTotal(compra.getTotal());
        response.setImpuestos(compra.getImpuestos());

        ClienteResponse clienteResponse = new ClienteResponse();
        clienteResponse.setDocumento(cliente.getDocumento());
        clienteResponse.setNombre(cliente.getNombre());
        clienteResponse.setTipoDocumento(cliente.getTipoDocumento().getNombre());
        response.setCliente(clienteResponse);

        List<ProductoResponse> productosResponse = detalleCompraRepository.findAll()
                .stream()
                .filter(detalle -> detalle.getCompra().getId().equals(compra.getId()))
                .map(detalle -> {
                    ProductoResponse productoResponse = new ProductoResponse();
                    Producto producto = detalle.getProducto();
                    productoResponse.setReferencia(producto.getReferencia());
                    productoResponse.setNombre(producto.getNombre());
                    productoResponse.setCantidad(detalle.getCantidad());
                    productoResponse.setPrecio(producto.getPrecio());
                    productoResponse.setDescuento(detalle.getDescuento());
                    productoResponse.setSubtotal(detalle.getPrecio());
                    return productoResponse;
                })
                .toList();
        response.setProductos(productosResponse);

        CajeroResponse cajeroResponse = new CajeroResponse();
        cajeroResponse.setDocumento(cajero.getDocumento());
        cajeroResponse.setNombre(cajero.getNombre());
        response.setCajero(cajeroResponse);

        return response;
    }
}
