package trinity.play2learn.backend.activity.completarOracion.mappers;

import java.util.List;

import trinity.play2learn.backend.activity.completarOracion.dtos.request.WordCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.WordCompletarOracionResponseDto;
import trinity.play2learn.backend.activity.completarOracion.models.WordCompletarOracion;

public class WordCompletarOracionMapper {
    
    public static WordCompletarOracion toModel(WordCompletarOracionRequestDto wordDto) {
        return WordCompletarOracion.builder()
            .word(wordDto.getWord())
            .wordOrder(wordDto.getWordOrder())
            .isMissing(wordDto.getIsMissing())
            .build();
    }

    public static List<WordCompletarOracion> toModelList(List<WordCompletarOracionRequestDto> wordDtos) {
        return wordDtos
            .stream()
            .map(WordCompletarOracionMapper::toModel)
            .toList();
    }

    public static WordCompletarOracionResponseDto toDto(WordCompletarOracion word) {
        return WordCompletarOracionResponseDto.builder()
            .id(word.getId())
            .word(word.getWord())
            .order(word.getWordOrder())
            .isMissing(word.getIsMissing())
            .build();
    }

    public static List<WordCompletarOracionResponseDto> toDtoList(List<WordCompletarOracion> words) {
        return words
            .stream()
            .map(WordCompletarOracionMapper::toDto)
            .toList();
    }
}
