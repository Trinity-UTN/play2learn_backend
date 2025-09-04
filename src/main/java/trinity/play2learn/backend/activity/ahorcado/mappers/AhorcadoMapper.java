package trinity.play2learn.backend.activity.ahorcado.mappers;

import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class AhorcadoMapper {
    
    public static Ahorcado toModel(AhorcadoRequestDto ahorcadoDto , Subject subject) {
        return Ahorcado.builder()
            .name("Ahorcado")
            .description(ahorcadoDto.getDescription())
            .dificulty(ahorcadoDto.getDificulty())
            .maxTime(ahorcadoDto.getMaxTime())
            .subject(subject)
            .startDate(ahorcadoDto.getStartDate())
            .endDate(ahorcadoDto.getEndDate())
            .attempts(ahorcadoDto.getAttempts())
            .word(ahorcadoDto.getWord())
            .errorsPermited(ahorcadoDto.getErrorsPermited())
            .actualBalance(0.0)
            .initialBalance(ahorcadoDto.getInitialBalance())
            .typeReward((ahorcadoDto.getTypeReward() != null) ? ahorcadoDto.getTypeReward() : TypeReward.EQUITATIVO)
            .build();
    }

    public static AhorcadoResponseDto toDto(Ahorcado ahorcado) {
        return AhorcadoResponseDto.builder()
            .id(ahorcado.getId())
            .name("Ahorcado")
            .description(ahorcado.getDescription())
            .dificulty(ahorcado.getDificulty())
            .maxTime(ahorcado.getMaxTime())
            .subject(SubjectMapper.toSubjectDto(ahorcado.getSubject()))
            .startDate(ahorcado.getStartDate())
            .endDate(ahorcado.getEndDate())
            .attempts(ahorcado.getAttempts())
            .word(ahorcado.getWord())
            .errorsPermited(ahorcado.getErrorsPermited().getValue()) //Devuelvo el valor entero de los errores.
            .actualBalance(ahorcado.getActualBalance())
            .initialBalance(ahorcado.getInitialBalance())
            .typeReward(ahorcado.getTypeReward())
            .build();
    }
}
