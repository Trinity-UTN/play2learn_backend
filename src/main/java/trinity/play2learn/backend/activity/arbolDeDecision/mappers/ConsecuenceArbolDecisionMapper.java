package trinity.play2learn.backend.activity.arbolDeDecision.mappers;

import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ConsecuenceArbolDecisionRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ConsecuenceArbolDecisionResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.models.ConsecuenceArbolDecision;

public class ConsecuenceArbolDecisionMapper {
    
    public static ConsecuenceArbolDecision toModel(ConsecuenceArbolDecisionRequestDto consecuenceDto) {
        if(consecuenceDto == null) return null;
        return ConsecuenceArbolDecision.builder()
            .name(consecuenceDto.getName())
            .approvesActivity(consecuenceDto.isApprovesActivity())
            .build();
    }

    public static ConsecuenceArbolDecisionResponseDto toDto(ConsecuenceArbolDecision consecuence) {
        return ConsecuenceArbolDecisionResponseDto.builder()
            .id(consecuence.getId())
            .name(consecuence.getName())
            .approvesActivity(consecuence.isApprovesActivity())
            .build();
    }
}
