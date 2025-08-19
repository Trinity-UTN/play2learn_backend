package trinity.play2learn.backend.activity.completarOracion.mappers;

import trinity.play2learn.backend.activity.activity.models.TypeReward;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.activity.completarOracion.models.CompletarOracionActivity;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class CompletarOracionActivityMapper {

    public static CompletarOracionActivity toModel(CompletarOracionActivityRequestDto activityDto , Subject subject) {
        CompletarOracionActivity activity = CompletarOracionActivity.builder()
                .description(activityDto.getDescription())
                .dificulty(activityDto.getDificulty())
                .maxTime(activityDto.getMaxTime())
                .subject(subject)
                .startDate(activityDto.getStartDate())
                .endDate(activityDto.getEndDate())
                .attempts(activityDto.getAttempts())
                .actualBalance(activityDto.getInitialBalance())
                .initialBalance(activityDto.getInitialBalance())
                .typeReward(TypeReward.EQUITATIVO)
                .build();

        activity.setSentences(SentenceCompletarOracionMapper.toModelList(activityDto.getSentences())); // Relaciono cada oracion con la actividad

        return activity;
    }

    public static CompletarOracionActivityResponseDto toDto(CompletarOracionActivity activity) {
        return CompletarOracionActivityResponseDto.builder()
                .id(activity.getId())
                .name(activity.getClass().getSimpleName())
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
                .build();
    }
}
