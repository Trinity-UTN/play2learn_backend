package trinity.play2learn.backend.activity.clasificacion.mappers;

import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.models.ClasificacionActivity;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class ClasificacionActivityMapper {
    
    public static ClasificacionActivity toModel(ClasificacionActivityRequestDto activityDto, Subject subject) {
        ClasificacionActivity activity = ClasificacionActivity.builder()
            .name("Desafio de clasificacion")
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

        activity.setCategories(CategoryClasificacionMapper.toModelList(activityDto.getCategories())); // Relaciono cada categoria con la actividad

        return activity;
    }

    public static ClasificacionActivityResponseDto toDto(ClasificacionActivity activity) {
        return ClasificacionActivityResponseDto.builder()
            .id(activity.getId())
            .name("Desafio de clasificacion")
            .description(activity.getDescription())
            .dificulty(activity.getDificulty())
            .maxTime(activity.getMaxTime())
            .startDate(activity.getStartDate())
            .endDate(activity.getEndDate())
            .attempts(activity.getAttempts())
            .subject(SubjectMapper.toSubjectDto(activity.getSubject())) 
            .categories(CategoryClasificacionMapper.toDtoList(activity.getCategories()))
            .actualBalance(activity.getActualBalance())
            .initialBalance(activity.getInitialBalance())
            .build();
    }
}
