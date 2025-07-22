package trinity.play2learn.backend.activity.preguntados.Mappers;

import java.util.List;

import trinity.play2learn.backend.activity.preguntados.dtos.request.OptionRequestDto;
import trinity.play2learn.backend.activity.preguntados.models.Option;

public class OptionMapper {
    
    public static Option toModel(OptionRequestDto optionDto) {
        return Option.builder()
            .option(optionDto.getOption())
            .build();
    }

    public static List<Option> toModelList(List<OptionRequestDto> optionDtos) {
        return optionDtos
            .stream()
            .map(OptionMapper::toModel)
            .toList();
    }

    public static List<String> toStringList(List<Option> options) {
        return options
            .stream()
            .map(Option::getOption)
            .toList();
    }
}
