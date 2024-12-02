package DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedioPagoDTO {
    private String tipoPago;
    private String tipoTarjeta;
    private int cuotas;
    private double valor;

    // Getters y Setters
}

