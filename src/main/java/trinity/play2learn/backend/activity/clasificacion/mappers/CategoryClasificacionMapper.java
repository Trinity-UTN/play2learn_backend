package trinity.play2learn.backend.activity.clasificacion.mappers;

import java.util.List;
import java.util.stream.Collectors;

import trinity.play2learn.backend.activity.clasificacion.dtos.request.CategoryClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.CategoryClasificacionResponseDto;
import trinity.play2learn.backend.activity.clasificacion.models.CategoryClasificacion;

public class CategoryClasificacionMapper {
    
    public static CategoryClasificacion toModel(CategoryClasificacionRequestDto categoryDto) {
        CategoryClasificacion category = CategoryClasificacion.builder()
            .name(categoryDto.getName())
            .build();

        category.setConcepts(ConceptClasificacionMapper.toModelList(categoryDto.getConcepts())); //Relaciona cada concepto con su categoria

        return category;
    }

    public static List<CategoryClasificacion> toModelList(List<CategoryClasificacionRequestDto> categoryDtos) {
        return categoryDtos
            .stream()
            .map(CategoryClasificacionMapper::toModel)
            .collect(Collectors.toList());
    }

    public static CategoryClasificacionResponseDto toDto(CategoryClasificacion category) {
        return CategoryClasificacionResponseDto.builder()
            .id(category.getId())
            .name(category.getName())
            .concepts(ConceptClasificacionMapper.toDtoList(category.getConcepts()))
            .build();
    }

    public static List<CategoryClasificacionResponseDto> toDtoList(List<CategoryClasificacion> categories) {
        return categories
            .stream()
            .map(CategoryClasificacionMapper::toDto)
            .collect(Collectors.toList());
    }
}
