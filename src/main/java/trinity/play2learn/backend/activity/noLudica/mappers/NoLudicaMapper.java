package trinity.play2learn.backend.activity.noLudica.mappers;

import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.activity.noLudica.models.NoLudica;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class NoLudicaMapper {

    public static NoLudica toModel (NoLudicaRequestDto dto, Subject subject) {
        return NoLudica.builder()
                .description(dto.getDescription())
                .dificulty(dto.getDificulty())
                .maxTime(dto.getMaxTime())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .attempts(dto.getAttempts())
                .subject(subject)
                .excercise(dto.getExcercise())
                .tipoEntrega(dto.getTipoEntrega())
                .build();
    }

    public static NoLudicaResponseDto toDto(NoLudica noLudica) {
        return NoLudicaResponseDto.builder()
                .id(noLudica.getId())
                .description(noLudica.getDescription())
                .name(noLudica.getClass().getSimpleName())
                .dificulty(noLudica.getDificulty())
                .maxTime(noLudica.getMaxTime())
                .startDate(noLudica.getStartDate())
                .endDate(noLudica.getEndDate())
                .attempts(noLudica.getAttempts())
                .subject(SubjectMapper.toSubjectDto(noLudica.getSubject()))
                .excercise(noLudica.getExcercise())
                .tipoEntrega(noLudica.getTipoEntrega().name())
                .build();
    }
    
}
