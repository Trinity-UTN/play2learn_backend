package trinity.play2learn.backend.activity.preguntados.Mappers;

import java.util.List;

import trinity.play2learn.backend.activity.preguntados.dtos.request.OptionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.OptionResponseDto;
import trinity.play2learn.backend.activity.preguntados.models.Option;

public class OptionMapper {
    
    public static Option toModel(OptionRequestDto optionDto) {
        return Option.builder()
            .option(optionDto.getOption())
            .isCorrect(optionDto.getIsCorrect() == null ? false : optionDto.getIsCorrect()) //Si el campo viene null, lo convierto en false
            .build();
    }

    public static List<Option> toModelList(List<OptionRequestDto> optionDtos) {
        return optionDtos
            .stream()
            .map(OptionMapper::toModel)
            .toList();
    }

    public static OptionResponseDto toDto(Option option) {
        return OptionResponseDto.builder()
            .id(option.getId())
            .option(option.getOption())
            .isCorrect(option.getIsCorrect())
            .build();
    }

    public static List<OptionResponseDto> toDtoList(List<Option> options) {
        return options
            .stream()
            .map(OptionMapper::toDto)
            .toList();
    }
}
