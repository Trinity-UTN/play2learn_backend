package trinity.play2learn.backend.activity.noLudica;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.activity.noLudica.models.NoLudica;
import trinity.play2learn.backend.activity.noLudica.models.TipoEntrega;
import trinity.play2learn.backend.admin.subject.models.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoLudicaTestMother {

    public static final String DEFAULT_EXERCISE = "Ejercicio de práctica";
    public static final String MAX_LENGTH_EXERCISE = "a".repeat(300);
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static NoLudicaRequestDto noLudicaRequestDto(
        String exercise,
        TipoEntrega tipoEntrega
    ) {
        LocalDateTime now = LocalDateTime.now();
        NoLudicaRequestDto dto = new NoLudicaRequestDto(
            exercise,
            tipoEntrega != null ? tipoEntrega : TipoEntrega.ENTREGA
        );
        dto.setDescription("Descripción de la actividad no lúdica");
        dto.setDifficulty(Difficulty.FACIL);
        dto.setMaxTime(30);
        dto.setStartDate(now.plusDays(1));
        dto.setEndDate(now.plusDays(7));
        dto.setAttempts(3);
        dto.setSubjectId(ActivityTestMother.SUBJECT_ID);
        dto.setInitialBalance(DEFAULT_INITIAL_BALANCE);
        return dto;
    }

    public static NoLudicaRequestDto validNoLudicaRequestDto() {
        return noLudicaRequestDto(DEFAULT_EXERCISE, TipoEntrega.ENTREGA);
    }

    public static NoLudicaRequestDto noLudicaRequestDtoWithExercise(String exercise) {
        return noLudicaRequestDto(exercise, TipoEntrega.ENTREGA);
    }

    public static NoLudicaRequestDto noLudicaRequestDtoWithTipoEntrega(TipoEntrega tipoEntrega) {
        return noLudicaRequestDto(DEFAULT_EXERCISE, tipoEntrega);
    }

    public static NoLudicaResponseDto noLudicaResponseDto(
        Long id,
        String exercise,
        String tipoEntrega
    ) {
        return NoLudicaResponseDto.builder()
            .id(id)
            .name("No Ludica")
            .description(DEFAULT_EXERCISE)
            .excercise(exercise != null ? exercise : DEFAULT_EXERCISE)
            .tipoEntrega(tipoEntrega != null ? tipoEntrega : TipoEntrega.ENTREGA.name())
            .build();
    }

    public static NoLudicaResponseDto validNoLudicaResponseDto(Long id) {
        return noLudicaResponseDto(id, DEFAULT_EXERCISE, TipoEntrega.ENTREGA.name());
    }

    public static NoLudica savedNoLudica(Long id, Subject subject) {
        return savedNoLudica(id, subject, DEFAULT_EXERCISE, TipoEntrega.ENTREGA);
    }

    public static NoLudica savedNoLudica(
        Long id,
        Subject subject,
        String exercise,
        TipoEntrega tipoEntrega
    ) {
        return NoLudica.builder()
            .id(id)
            .name("No Ludica")
            .description(exercise != null ? exercise : DEFAULT_EXERCISE)
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .excercise(exercise != null ? exercise : DEFAULT_EXERCISE)
            .tipoEntrega(tipoEntrega != null ? tipoEntrega : TipoEntrega.ENTREGA)
            .build();
    }
}

