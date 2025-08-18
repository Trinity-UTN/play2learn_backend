package trinity.play2learn.backend.activity.arbolDeDecision.dtos.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DecisionArbolDecisionRequestDto {

    @NotBlank(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 200, message = ValidationMessages.NAME_200_MAX_LENGHT)
    private String name;

    @Size(max = 500, message = ValidationMessages.CONTEXT_500_MAX_LENGHT)
    private String context;

    // Puede ser nula, en caso de no serlo debe tener 2 opciones
    @Valid
    @JsonSetter(nulls = Nulls.AS_EMPTY) //Si options llega nulo, se setea como un array vacio (Evita nullPointerException)
    @Builder.Default
    private List<DecisionArbolDecisionRequestDto> options = new ArrayList<>();

    private ConsecuenceArbolDecisionRequestDto consecuence;

    @AssertTrue(message = ValidationMessages.OPTIONS_SIZE)
    public boolean isOptionsSizeValid() {
        if (options.size() != 2 && !options.isEmpty()) {
            return false;
        }
        
        return true;
    }

    @AssertTrue(message = ValidationMessages.OPTIONS_AND_CONSECUENCE_NULL)
    public boolean hasOptionsOrConsecuence() {
        if (options.isEmpty() && consecuence == null) {
            return false;
        }
        
        return true;
    }

}
