package DTOs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCompraDTO {
	private String referencia;
    private Integer cantidad;
    private Double descuento;
}
