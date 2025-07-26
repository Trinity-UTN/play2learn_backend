package trinity.play2learn.backend.activity.clasificacion.mappers;

import java.util.List;

import trinity.play2learn.backend.activity.clasificacion.dtos.request.ConceptClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ConceptClasificacionResponseDto;
import trinity.play2learn.backend.activity.clasificacion.models.ConceptClasificacion;

public class ConceptClasificacionMapper {
 
    public static ConceptClasificacion toModel(ConceptClasificacionRequestDto conceptDto) {
        return ConceptClasificacion.builder()
            .name(conceptDto.getName())
            .build();
    }

    public static List<ConceptClasificacion> toModelList(List<ConceptClasificacionRequestDto> conceptDtos) {
        return conceptDtos
            .stream()
            .map(ConceptClasificacionMapper::toModel)
            .toList();
    }

    public static ConceptClasificacionResponseDto toDto(ConceptClasificacion concept) {
        return ConceptClasificacionResponseDto.builder()
            .id(concept.getId())
            .name(concept.getName())
            .build();
    }

    public static List<ConceptClasificacionResponseDto> toDtoList(List<ConceptClasificacion> concepts) {
        return concepts
            .stream()
            .map(ConceptClasificacionMapper::toDto)
            .toList();
    }
}
