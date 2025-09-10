package trinity.play2learn.backend.activity.arbolDeDecision.mappers;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.models.ArbolDeDecisionActivity;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Component("arbolDeDecisionActivityMapper")
public class ArbolDeDecisionMapper implements IActivityMapper {
    
    public static ArbolDeDecisionActivity toModel(ArbolDeDecisionActivityRequestDto activityDto , Subject subject) {
        ArbolDeDecisionActivity activity = ArbolDeDecisionActivity.builder()
            .name("Arbol de decision")
            .description(activityDto.getDescription())
            .dificulty(activityDto.getDificulty())
            .maxTime(activityDto.getMaxTime())
            .subject(subject)
            .startDate(activityDto.getStartDate())
            .endDate(activityDto.getEndDate())
            .attempts(activityDto.getAttempts())
            .introduction(activityDto.getIntroduction())
            .actualBalance(0.0)
            .initialBalance(activityDto.getInitialBalance())
            .typeReward((activityDto.getTypeReward() != null) ? activityDto.getTypeReward() : TypeReward.EQUITATIVO)
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
            .typeReward(activity.getTypeReward())
            .decisionTree(DecisionArbolDecisionMapper.toDtoList(activity.getDecisionTree()))
            .build();
    }

    @Override
    public ActivityResponseDto toActivityDto(Activity activity) {

        //Si la instancia recibida no es de tipo Ahorcado, lanza una exception
        if (!(activity instanceof ArbolDeDecisionActivity)) {
            throw new IllegalArgumentException("Expected ArbolDeDecisionActivity, got: " + activity.getClass());
        }

        //Casteo la actividad recibida a ArbolDeDecision
        ArbolDeDecisionActivity arbolDeDecisionActivity = (ArbolDeDecisionActivity) activity;
        return toDto(arbolDeDecisionActivity);

    }

}
