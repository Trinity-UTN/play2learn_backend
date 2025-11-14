package trinity.play2learn.backend.activity.clasificacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.CategoryClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ConceptClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.models.ClasificacionActivity;
import trinity.play2learn.backend.admin.subject.models.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClasificacionTestMother {

    public static final String DEFAULT_CATEGORY_NAME = "Categoría";
    public static final String DEFAULT_CONCEPT_NAME = "Concepto";
    public static final String MAX_LENGTH_CATEGORY_NAME = "a".repeat(50);
    public static final String MAX_LENGTH_CONCEPT_NAME = "a".repeat(100);
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static ClasificacionActivityRequestDto clasificacionRequestDto(
        List<CategoryClasificacionRequestDto> categories
    ) {
        LocalDateTime now = LocalDateTime.now();
        ClasificacionActivityRequestDto dto = new ClasificacionActivityRequestDto(
            categories != null ? categories : List.of()
        );
        dto.setDescription("Descripción de la actividad de clasificación");
        dto.setStartDate(now.plusDays(1));
        dto.setEndDate(now.plusDays(7));
        dto.setDifficulty(Difficulty.FACIL);
        dto.setMaxTime(30);
        dto.setSubjectId(ActivityTestMother.SUBJECT_ID);
        dto.setAttempts(3);
        dto.setInitialBalance(DEFAULT_INITIAL_BALANCE);
        return dto;
    }

    public static ClasificacionActivityRequestDto validClasificacionRequestDto() {
        return clasificacionRequestDto(
            List.of(
                categoryWithConcepts("Categoría 1", 2),
                categoryWithConcepts("Categoría 2", 2)
            )
        );
    }

    public static ClasificacionActivityRequestDto clasificacionRequestDtoWithDuplicateCategoryNames() {
        return clasificacionRequestDto(
            List.of(
                categoryWithConcepts("Categoría", 2),
                categoryWithConcepts("Categoría", 2)
            )
        );
    }

    public static ClasificacionActivityRequestDto clasificacionRequestDtoWithDuplicateConceptNames() {
        return clasificacionRequestDto(
            List.of(
                categoryWithConcepts("Categoría 1", List.of("Concepto", "Concepto 2")),
                categoryWithConcepts("Categoría 2", List.of("Concepto 3", "Concepto 4"))
            )
        );
    }

    public static CategoryClasificacionRequestDto categoryWithConcepts(String name, int conceptCount) {
        List<ConceptClasificacionRequestDto> concepts = new ArrayList<>();
        for (int i = 1; i <= conceptCount; i++) {
            concepts.add(concept(name + " - Concepto " + i));
        }
        return CategoryClasificacionRequestDto.builder()
            .name(name)
            .concepts(concepts)
            .build();
    }

    public static CategoryClasificacionRequestDto categoryWithConcepts(String name, List<String> conceptNames) {
        List<ConceptClasificacionRequestDto> concepts = conceptNames.stream()
            .map(ClasificacionTestMother::concept)
            .toList();
        return CategoryClasificacionRequestDto.builder()
            .name(name)
            .concepts(concepts)
            .build();
    }

    public static CategoryClasificacionRequestDto categoryWithoutConcepts(String name) {
        return CategoryClasificacionRequestDto.builder()
            .name(name)
            .concepts(new ArrayList<>())
            .build();
    }

    public static ConceptClasificacionRequestDto concept(String name) {
        return ConceptClasificacionRequestDto.builder()
            .name(name)
            .build();
    }

    public static ClasificacionActivityResponseDto clasificacionResponseDto(
        Long id,
        List<?> categories
    ) {
        return ClasificacionActivityResponseDto.builder()
            .id(id)
            .name("Clasificacion")
            .description("Descripción de la actividad de clasificación")
            .categories(categories != null ? (List) categories : List.of())
            .build();
    }

    public static ClasificacionActivityResponseDto validClasificacionResponseDto(Long id) {
        return clasificacionResponseDto(id, List.of());
    }

    public static ClasificacionActivity savedClasificacion(Long id, Subject subject) {
        return ClasificacionActivity.builder()
            .id(id)
            .name("Clasificacion")
            .description("Descripción de la actividad de clasificación")
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .categories(List.of())
            .build();
    }
}

