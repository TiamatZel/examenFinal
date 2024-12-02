package DTOs;

import lombok.Data;

import java.util.List;

@Data
public class FacturaResponse {
    private double total;
    private double impuestos;
    private ClienteResponse cliente;
    private List<ProductoResponse> productos;
    private CajeroResponse cajero;
}
