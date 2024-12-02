package DTOs;

import lombok.Data;

@Data
public class PagoRequest {
    private String tipoPago;
    private String tipoTarjeta;
    private int cuotas;
    private double valor;
}

