package trinity.play2learn.backend.activity.memorama.mappers;

import java.util.List;

import trinity.play2learn.backend.activity.memorama.dtos.CouplesMemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.models.CouplesMemorama;

public class CouplesMemoramaMapper {

    public static CouplesMemorama toModel (String url, String concepto, Memorama memorama) {
        return CouplesMemorama.builder()
                .url(url)
                .concept(concepto)
                .memorama(memorama)
                .build();
    }

    public static CouplesMemoramaResponseDto toDto(CouplesMemorama pareja) {
        return CouplesMemoramaResponseDto.builder()
                .id(pareja.getId())
                .image(pareja.getUrl())
                .concept(pareja.getConcept())
                .build();
    }

    public static List<CouplesMemoramaResponseDto> toDtoList(List<CouplesMemorama> parejas) {
        return parejas.stream().map(CouplesMemoramaMapper::toDto).toList();
    }
    
}
