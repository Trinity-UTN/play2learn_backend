package trinity.play2learn.backend.activity.completarOracion.mappers;

import java.util.List;
import java.util.stream.Collectors;

import trinity.play2learn.backend.activity.completarOracion.dtos.request.SentenceCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.SentenceCompletarOracionResponseDto;
import trinity.play2learn.backend.activity.completarOracion.models.SentenceCompletarOracion;

public class SentenceCompletarOracionMapper {
    
    public static SentenceCompletarOracion toModel(SentenceCompletarOracionRequestDto sentenceDto) {
        SentenceCompletarOracion sentence = SentenceCompletarOracion.builder()
            .build();
        
        sentence.setWords(WordCompletarOracionMapper.toModelList(sentenceDto.getWords())); //Relaciono cada palabra con la oracion
        
        return sentence;
    }

    public static List<SentenceCompletarOracion> toModelList(List<SentenceCompletarOracionRequestDto> sentenceDtos) {
        return sentenceDtos
            .stream()
            .map(SentenceCompletarOracionMapper::toModel)
            .collect(Collectors.toList());
    }

    public static SentenceCompletarOracionResponseDto toDto(SentenceCompletarOracion sentence) {
        return SentenceCompletarOracionResponseDto.builder()
            .id(sentence.getId())
            .completeSentence(sentence.getCompleteSentence())
            .words(WordCompletarOracionMapper.toDtoList(sentence.getWords()))
            .build();
    }

    public static List<SentenceCompletarOracionResponseDto> toDtoList(List<SentenceCompletarOracion> sentences) {
        return sentences
            .stream()
            .map(SentenceCompletarOracionMapper::toDto)
            .collect(Collectors.toList());
    }
}
