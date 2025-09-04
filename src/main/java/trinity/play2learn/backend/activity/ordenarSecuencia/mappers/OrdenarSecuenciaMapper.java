package trinity.play2learn.backend.activity.ordenarSecuencia.mappers;

import trinity.play2learn.backend.activity.activity.models.TypeReward;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class OrdenarSecuenciaMapper {

    public static OrdenarSecuencia toModel (OrdenarSecuenciaRequestDto dto, Subject subject) {
        return OrdenarSecuencia.builder()
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
            .build();
    }
    
}
