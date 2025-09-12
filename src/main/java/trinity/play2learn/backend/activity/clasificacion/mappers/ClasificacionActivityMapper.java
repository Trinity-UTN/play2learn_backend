package trinity.play2learn.backend.activity.clasificacion.mappers;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.models.ClasificacionActivity;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Component("clasificacionActivityMapper")
public class ClasificacionActivityMapper implements IActivityMapper{
    
    public static ClasificacionActivity toModel(ClasificacionActivityRequestDto activityDto, Subject subject) {
        ClasificacionActivity activity = ClasificacionActivity.builder()
            .name("Desafio de clasificacion")
            .description(activityDto.getDescription())
            .difficulty(activityDto.getDifficulty())
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
            .difficulty(activity.getDifficulty())
            .maxTime(activity.getMaxTime())
            .startDate(activity.getStartDate())
            .endDate(activity.getEndDate())
            .attempts(activity.getAttempts())
            .subject(SubjectMapper.toSimplifiedDto(activity.getSubject())) 
            .categories(CategoryClasificacionMapper.toDtoList(activity.getCategories()))
            .actualBalance(activity.getActualBalance())
            .initialBalance(activity.getInitialBalance())
            .typeReward(activity.getTypeReward())
            .build();
    }

    @Override
    public ActivityResponseDto toActivityDto(Activity activity) {

        if (!(activity instanceof ClasificacionActivity)) {
            throw new IllegalArgumentException("Expected ClasificacionActivity, got: " + activity.getClass());
        }

        ClasificacionActivity clasificacionActivity = (ClasificacionActivity) activity;
        return toDto(clasificacionActivity);
    }
}
