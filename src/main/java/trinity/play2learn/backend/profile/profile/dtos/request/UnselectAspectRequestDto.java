package trinity.play2learn.backend.profile.profile.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;

@Data
@AllArgsConstructor
@Builder
public class UnselectAspectRequestDto {

    @NotNull
    private TypeAspect typeAspect;

    @NotNull
    private Long profileId;
}
