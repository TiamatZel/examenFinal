package DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String documento;
    private String nombre;
    private String tipoDocumento;

    // Getters y Setters
}

