package trinity.play2learn.backend.activity.ordenarSecuencia.mappers;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Component("ordenarSecuenciaMapper")
public class OrdenarSecuenciaMapper implements IActivityMapper{

    public static OrdenarSecuencia toModel (OrdenarSecuenciaRequestDto dto, Subject subject) {
        return OrdenarSecuencia.builder()
            .name("Ordenar Secuencia")
            .description(dto.getDescription())
            .dificulty(dto.getDificulty())
            .maxTime(dto.getMaxTime())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .attempts(dto.getAttempts())
            .subject(subject)
            .actualBalance(0.0)
            .initialBalance(dto.getInitialBalance())
            .typeReward((dto.getTypeReward() != null) ? dto.getTypeReward() : TypeReward.EQUITATIVO)
            .build();
    }

    public static OrdenarSecuenciaResponseDto toDto (OrdenarSecuencia ordenarSecuencia) {
        return OrdenarSecuenciaResponseDto.builder()
            .id(ordenarSecuencia.getId())
            .name("Ordenar Secuencia")
            .description(ordenarSecuencia.getDescription())
            .dificulty(ordenarSecuencia.getDificulty())
            .maxTime(ordenarSecuencia.getMaxTime())
            .startDate(ordenarSecuencia.getStartDate())
            .endDate(ordenarSecuencia.getEndDate())
            .attempts(ordenarSecuencia.getAttempts())
            .subject(SubjectMapper.toSubjectDto(ordenarSecuencia.getSubject()))
            .events(EventMapper.toDtoList(ordenarSecuencia.getEvents()))
            .actualBalance(ordenarSecuencia.getActualBalance())
            .initialBalance(ordenarSecuencia.getInitialBalance())
            .typeReward(ordenarSecuencia.getTypeReward())
            .build();
    }

    @Override
    public ActivityResponseDto toActivityDto(Activity activity) {
        if (!(activity instanceof OrdenarSecuencia)) {
            throw new IllegalArgumentException("Expected OrdenarSecuencia, got: " + activity.getClass());
        }
        OrdenarSecuencia ordenarSecuencia = (OrdenarSecuencia) activity;
        return toDto(ordenarSecuencia);
    }
    
}
