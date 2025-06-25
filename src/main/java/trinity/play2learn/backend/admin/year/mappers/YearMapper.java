package trinity.play2learn.backend.admin.year.mappers;

import java.util.ArrayList;
import java.util.List;

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

    public static List<YearResponseDto> toListDto(Iterable<Year> years) {
        List<YearResponseDto> yearDtos = new ArrayList<>();
        for (Year year : years) {
            yearDtos.add(toDto(year));
        }
        return yearDtos;
    }
}
