package DTOs;

import lombok.Data;

@Data
public class ProductoResponse {
    private String referencia;
    private String nombre;
    private int cantidad;
    private double precio;
    private double descuento;
    private double subtotal;
}

