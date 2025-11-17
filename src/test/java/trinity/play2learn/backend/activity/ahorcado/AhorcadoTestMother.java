package trinity.play2learn.backend.activity.ahorcado;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.admin.subject.models.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AhorcadoTestMother {

    public static final String DEFAULT_WORD = "palabra";
    public static final String VALID_SPANISH_WORD = "niño áéíóú";
    public static final String MAX_LENGTH_WORD = "a".repeat(50);
    public static final String WORD_WITH_NUMBERS = "palabra123";
    public static final String WORD_WITH_SPECIAL_CHARS = "palabra@test";
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static AhorcadoRequestDto ahorcadoRequestDto(String word, Errors errorsPermited) {
        LocalDateTime now = LocalDateTime.now();
        AhorcadoRequestDto dto = new AhorcadoRequestDto(
            word != null ? word : DEFAULT_WORD,
            errorsPermited != null ? errorsPermited : Errors.TRES
        );
        dto.setDescription("Descripción del ahorcado");
        dto.setStartDate(now.plusDays(1));
        dto.setEndDate(now.plusDays(7));
        dto.setDifficulty(Difficulty.FACIL);
        dto.setMaxTime(30);
        dto.setSubjectId(ActivityTestMother.SUBJECT_ID);
        dto.setAttempts(3);
        dto.setInitialBalance(DEFAULT_INITIAL_BALANCE);
        return dto;
    }

    public static AhorcadoRequestDto validAhorcadoRequestDto() {
        return ahorcadoRequestDto(DEFAULT_WORD, Errors.TRES);
    }

    public static AhorcadoRequestDto ahorcadoRequestDtoWithErrors(Errors errorsPermited) {
        return ahorcadoRequestDto(DEFAULT_WORD, errorsPermited);
    }

    public static AhorcadoResponseDto ahorcadoResponseDto(Long id, String word, int errorsPermited) {
        return AhorcadoResponseDto.builder()
            .id(id)
            .name("Ahorcado")
            .description("Descripción del ahorcado")
            .word(word)
            .errorsPermited(errorsPermited)
            .build();
    }

    public static AhorcadoResponseDto validAhorcadoResponseDto(Long id) {
        return ahorcadoResponseDto(id, DEFAULT_WORD, Errors.TRES.getValue());
    }

    public static Ahorcado savedAhorcado(Long id, Subject subject, String word, Errors errorsPermited) {
        return Ahorcado.builder()
            .id(id)
            .name("Ahorcado")
            .description("Descripción del ahorcado")
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .word(word)
            .errorsPermited(errorsPermited)
            .build();
    }

    public static Ahorcado savedAhorcado(Long id, Subject subject) {
        return savedAhorcado(id, subject, DEFAULT_WORD, Errors.TRES);
    }
}

