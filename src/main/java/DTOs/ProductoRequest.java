package DTOs;

import lombok.Data;

@Data
public class ProductoRequest {
    private String referencia;
    private int cantidad;
    private double descuento;
}

