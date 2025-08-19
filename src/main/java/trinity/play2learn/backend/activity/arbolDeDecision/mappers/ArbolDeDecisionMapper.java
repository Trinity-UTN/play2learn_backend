package trinity.play2learn.backend.activity.arbolDeDecision.mappers;

import trinity.play2learn.backend.activity.activity.models.TypeReward;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.models.ArbolDeDecisionActivity;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class ArbolDeDecisionMapper {
    
    public static ArbolDeDecisionActivity toModel(ArbolDeDecisionActivityRequestDto activityDto , Subject subject) {
        ArbolDeDecisionActivity activity = ArbolDeDecisionActivity.builder()
            .description(activityDto.getDescription())
            .dificulty(activityDto.getDificulty())
            .maxTime(activityDto.getMaxTime())
            .subject(subject)
            .startDate(activityDto.getStartDate())
            .endDate(activityDto.getEndDate())
            .attempts(activityDto.getAttempts())
            .introduction(activityDto.getIntroduction())
            .actualBalance(activityDto.getInitialBalance())
            .initialBalance(activityDto.getInitialBalance())
            .typeReward(TypeReward.EQUITATIVO)
            .build();
        
        activity.setDecisionTree(DecisionArbolDecisionMapper.toModelList(activityDto.getDecisionTree())) ; // Relaciono cada decision con la actividad

        return activity;
    }

    public static ArbolDeDecisionActivityResponseDto toDto(ArbolDeDecisionActivity activity) {
        return ArbolDeDecisionActivityResponseDto.builder()
            .id(activity.getId())
            .name("Arbol de decision")
            .description(activity.getDescription())
            .dificulty(activity.getDificulty())
            .maxTime(activity.getMaxTime())
            .subject(SubjectMapper.toSubjectDto(activity.getSubject()))
            .startDate(activity.getStartDate())
            .endDate(activity.getEndDate())
            .attempts(activity.getAttempts())
            .introduction(activity.getIntroduction())
            .actualBalance(activity.getActualBalance())
            .initialBalance(activity.getInitialBalance())
            .decisionTree(DecisionArbolDecisionMapper.toDtoList(activity.getDecisionTree()))
            .build();}





}
