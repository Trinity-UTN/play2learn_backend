package trinity.play2learn.backend.admin.year.mappers;

import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.models.Year;

public class YearMapper {

    public static Year toModel(YearRequestDto yearDto) {
        return Year.builder()
            .name(yearDto.getName())
            .build();
    }

    public static YearResponseDto toDto(Year year) {
        return YearResponseDto.builder()
            .id(year.getId())
            .name(year.getName())
            .build();
    }
}
