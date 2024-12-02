package DTOs;

import lombok.Data;

@Data
public class FacturaConsultaRequest {
    private String token;
    private String cliente;
    private Long factura;
}

