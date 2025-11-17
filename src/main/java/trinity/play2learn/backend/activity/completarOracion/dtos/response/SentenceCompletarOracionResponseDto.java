package trinity.play2learn.backend.activity.completarOracion.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentenceCompletarOracionResponseDto {

    private Long id;
    private String completeSentence;
    private List<WordCompletarOracionResponseDto> words;
}
