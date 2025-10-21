package trinity.play2learn.backend.investment.stock.dtos.request;

import java.math.BigInteger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRegisterRequestDto {

    @NotBlank (message = "El campo nombre no puede estar vacío")
    @Size (min = 1, max = 100, message = "La longitud máxima es de 100")
    private String name;
    
    @NotBlank (message = "El campo abreviatura no puede estar vacío")
    @Size (min = 1, max = 10, message = "La longitud máxima es de 10")
    private String abbreviation;
    
    @Positive
    @NotNull (message = "El campo precio inicial no puede estar vacío")
    private Double initialPrice;

    @Positive
    @NotNull (message = "El campo cantidad total de acciones no puede estar vacío")
    private BigInteger totalAmount;

    @NotNull (message = "El campo nivel de riesgo no puede estar vacío")
    private RiskLevel riskLevel;
    
}
