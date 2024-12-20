package com.ufps.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufps.services.FacturaService;

import DTOs.ConsultaFacturaRequest;
import DTOs.FacturaRequest;
import DTOs.FacturaResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/crear")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @PostMapping("/{tiendaId}")
    public ResponseEntity<?> crearFactura(@PathVariable String tiendaId, @RequestBody FacturaRequest request) {
        facturaService.procesarFactura(tiendaId, request);
        return ResponseEntity.ok("Factura registrada exitosamente");
    }
    
    @PostMapping("/consultar/{tiendaUuid}")
    public ResponseEntity<FacturaResponse> consultarFactura(
            @PathVariable String tiendaUuid,
            @RequestBody ConsultaFacturaRequest consultaFacturaRequest) {
        FacturaResponse facturaResponse = facturaService.consultarFactura(tiendaUuid, consultaFacturaRequest);
        return ResponseEntity.ok(facturaResponse);
    }
}
