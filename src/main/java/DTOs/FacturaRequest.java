package DTOs;

import lombok.Data;

import java.util.List;

@Data
public class FacturaRequest {
    private double impuesto;
    private ClienteRequest cliente;
    private List<ProductoRequest> productos;
    private List<PagoRequest> mediosPago;
    private VendedorRequest vendedor;
    private CajeroRequest cajero;
}
