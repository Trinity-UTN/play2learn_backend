package trinity.play2learn.backend.admin.classes.mappers;

import trinity.play2learn.backend.admin.classes.dtos.ClassRequestDto;
import trinity.play2learn.backend.admin.classes.dtos.ClassResponseDto;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.classes.models.Class;

public class ClassMapper {
    /**
     * Transforma un DTO de clase en un modelo de clase.
     *
     * @param ClassRequestDto classDto
     * @param Year year
     * @return Class
     */
    public static Class toModel(ClassRequestDto classDto, Year year) {
        return Class.builder()
            .name(classDto.getName())
            .year(year)
            .build();
    }
    /**
     * Transforma un modelo de clase en un DTO de respuesta de clase.
     *
     * @param Class classModel
     * @return ClassResponseDto
     */
    public static ClassResponseDto toDto(Class classModel) {
        return ClassResponseDto.builder()
            .id(classModel.getId())
            .name(classModel.getName())
            .year(YearMapper.toDto(classModel.getYear()))
            .build();
    }
}
