package trinity.play2learn.backend.investment.savingAccount.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountRegisterRequestDto {

    @Positive
    @NotNull (message = "El campo monto inicial no puede estar vacío")
    private Double initialAmount;

    @NotBlank (message = "El campo nombre no puede estar vacío")
    private String name;
    
}
