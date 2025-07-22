package trinity.play2learn.backend.activity.memorama.mappers;

import java.util.List;

import trinity.play2learn.backend.activity.memorama.dtos.ParejaMemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.models.ParejaMemorama;

public class ParejaMemoramaMapper {

    public static ParejaMemorama toModel (String url, String concepto, Memorama memorama) {
        return ParejaMemorama.builder()
                .url(url)
                .concepto(concepto)
                .memorama(memorama)
                .build();
    }

    public static ParejaMemoramaResponseDto toDto(ParejaMemorama pareja) {
        return ParejaMemoramaResponseDto.builder()
                .id(pareja.getId())
                .imagen(pareja.getUrl())
                .concepto(pareja.getConcepto())
                .build();
    }

    public static List<ParejaMemoramaResponseDto> toDtoList(List<ParejaMemorama> parejas) {
        return parejas.stream().map(ParejaMemoramaMapper::toDto).toList();
    }
    
}
