package trinity.play2learn.backend.activity.completarOracion.mappers;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.activity.completarOracion.models.CompletarOracionActivity;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Component("completarOracionActivityMapper")
public class CompletarOracionActivityMapper implements IActivityMapper{

    public static CompletarOracionActivity toModel(CompletarOracionActivityRequestDto activityDto , Subject subject) {
        CompletarOracionActivity activity = CompletarOracionActivity.builder()
                .name("Completar oracion")
                .description(activityDto.getDescription())
                .dificulty(activityDto.getDificulty())
                .maxTime(activityDto.getMaxTime())
                .subject(subject)
                .startDate(activityDto.getStartDate())
                .endDate(activityDto.getEndDate())
                .attempts(activityDto.getAttempts())
                .actualBalance(0.0)
                .initialBalance(activityDto.getInitialBalance())
                .typeReward((activityDto.getTypeReward() != null) ? activityDto.getTypeReward() : TypeReward.EQUITATIVO)
                .build();

        activity.setSentences(SentenceCompletarOracionMapper.toModelList(activityDto.getSentences())); // Relaciono cada oracion con la actividad

        return activity;
    }

    public static CompletarOracionActivityResponseDto toDto(CompletarOracionActivity activity) {
        return CompletarOracionActivityResponseDto.builder()
                .id(activity.getId())
                .name("Completar oracion")
                .description(activity.getDescription())
                .dificulty(activity.getDificulty())
                .maxTime(activity.getMaxTime())
                .subject(SubjectMapper.toSubjectDto(activity.getSubject()))
                .startDate(activity.getStartDate())
                .endDate(activity.getEndDate())
                .attempts(activity.getAttempts())
                .sentences(SentenceCompletarOracionMapper.toDtoList(activity.getSentences()))
                .initialBalance(activity.getInitialBalance())
                .actualBalance(activity.getActualBalance())
                .typeReward(activity.getTypeReward())
                .build();
    }

    @Override
    public ActivityResponseDto toActivityDto(Activity activity) {
        
        if (!(activity instanceof CompletarOracionActivity)) {
            throw new IllegalArgumentException("Expected CompletarOracionActivity, got: " + activity.getClass());
        }

        CompletarOracionActivity completarOracionActivity = (CompletarOracionActivity) activity;
        return toDto(completarOracionActivity);
    }
}
