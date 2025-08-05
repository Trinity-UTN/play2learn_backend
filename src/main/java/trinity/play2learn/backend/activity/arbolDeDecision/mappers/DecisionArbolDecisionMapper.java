package trinity.play2learn.backend.activity.arbolDeDecision.mappers;

import java.util.List;

import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.DecisionArbolDecisionRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.DecisionArbolDecisionResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.models.DecisionArbolDecision;

public class DecisionArbolDecisionMapper {
    
    public static DecisionArbolDecision toModel(DecisionArbolDecisionRequestDto decisionDto) {
        DecisionArbolDecision decision = DecisionArbolDecision.builder()
            .name(decisionDto.getName())
            .consecuence(ConsecuenceArbolDecisionMapper.toModel(decisionDto.getConsecuence()))
            .build();

        decision.setOptions(DecisionArbolDecisionMapper.toModelList(decisionDto.getOptions()));
        return decision;
    }

    public static List<DecisionArbolDecision> toModelList(List<DecisionArbolDecisionRequestDto> decisionDtos) {
        if(decisionDtos == null) return null;
        return decisionDtos
            .stream()
            .map(DecisionArbolDecisionMapper::toModel)
            .toList();
    }

    public static DecisionArbolDecisionResponseDto toDto(DecisionArbolDecision decision) {
        return DecisionArbolDecisionResponseDto.builder()
            .id(decision.getId())
            .name(decision.getName())
            .consecuence(decision.getConsecuence() == null ? null : ConsecuenceArbolDecisionMapper.toDto(decision.getConsecuence()))
            .options(DecisionArbolDecisionMapper.toDtoList(decision.getOptions()))
            .build();
    }

    public static List<DecisionArbolDecisionResponseDto> toDtoList(List<DecisionArbolDecision> decisions) {
        return decisions
            .stream()
            .map(DecisionArbolDecisionMapper::toDto)
            .toList();
    }


}
