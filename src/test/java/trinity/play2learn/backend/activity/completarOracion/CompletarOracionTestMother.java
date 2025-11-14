package trinity.play2learn.backend.activity.completarOracion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.SentenceCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.WordCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.activity.completarOracion.models.CompletarOracionActivity;
import trinity.play2learn.backend.admin.subject.models.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompletarOracionTestMother {

    public static final String DEFAULT_WORD = "palabra";
    public static final String MAX_LENGTH_WORD = "a".repeat(30);
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static CompletarOracionActivityRequestDto completarOracionRequestDto(
        List<SentenceCompletarOracionRequestDto> sentences
    ) {
        LocalDateTime now = LocalDateTime.now();
        return CompletarOracionActivityRequestDto.builder()
            .description("Descripción de la actividad de completar oración")
            .startDate(now.plusDays(1))
            .endDate(now.plusDays(7))
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subjectId(ActivityTestMother.SUBJECT_ID)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .sentences(sentences)
            .build();
    }

    public static CompletarOracionActivityRequestDto validCompletarOracionRequestDto() {
        return completarOracionRequestDto(
            List.of(validSentence())
        );
    }

    public static SentenceCompletarOracionRequestDto validSentence() {
        return sentenceWithWords(
            List.of(
                word("El", 0, false),
                word("gato", 1, true),
                word("come", 2, false)
            )
        );
    }

    public static SentenceCompletarOracionRequestDto sentenceWithWords(List<WordCompletarOracionRequestDto> words) {
        return SentenceCompletarOracionRequestDto.builder()
            .words(words)
            .build();
    }

    public static SentenceCompletarOracionRequestDto sentenceWithWords(String... wordsText) {
        List<WordCompletarOracionRequestDto> words = new ArrayList<>();
        for (int i = 0; i < wordsText.length; i++) {
            words.add(word(wordsText[i], i, i % 2 == 1));
        }
        return sentenceWithWords(words);
    }

    public static SentenceCompletarOracionRequestDto sentenceWithoutMissingWords(String... wordsText) {
        List<WordCompletarOracionRequestDto> words = new ArrayList<>();
        for (int i = 0; i < wordsText.length; i++) {
            words.add(word(wordsText[i], i, false));
        }
        return sentenceWithWords(words);
    }

    public static WordCompletarOracionRequestDto word(String word, Integer wordOrder, Boolean isMissing) {
        return WordCompletarOracionRequestDto.builder()
            .word(word)
            .wordOrder(wordOrder)
            .isMissing(isMissing)
            .build();
    }

    public static CompletarOracionActivityResponseDto completarOracionResponseDto(
        Long id,
        List<?> sentences
    ) {
        return CompletarOracionActivityResponseDto.builder()
            .id(id)
            .name("Completar oracion")
            .description("Descripción de la actividad de completar oración")
            .sentences(sentences != null ? (List) sentences : List.of())
            .build();
    }

    public static CompletarOracionActivityResponseDto validCompletarOracionResponseDto(Long id) {
        return completarOracionResponseDto(id, List.of());
    }

    public static CompletarOracionActivity savedCompletarOracion(Long id, Subject subject) {
        return CompletarOracionActivity.builder()
            .id(id)
            .name("Completar oracion")
            .description("Descripción de la actividad de completar oración")
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .sentences(List.of())
            .build();
    }
}

