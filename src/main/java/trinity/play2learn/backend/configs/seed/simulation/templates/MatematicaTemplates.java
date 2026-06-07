package trinity.play2learn.backend.configs.seed.simulation.templates;

import java.time.LocalDateTime;
import java.util.List;

import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.CategoryClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ConceptClasificacionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.OptionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;

/**
 * Plantillas de contenido temático para Matemática.
 */
public final class MatematicaTemplates {

    private MatematicaTemplates() {
    }

    public static final List<SimulationActivityType> SUPPORTED_TYPES = List.of(
        SimulationActivityType.PREGUNTADOS,
        SimulationActivityType.CLASIFICACION,
        SimulationActivityType.AHORCADO
    );

    public static PreguntadosRequestDto preguntados(
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        PreguntadosRequestDto dto = PreguntadosRequestDto.builder()
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(attempts)
            .initialBalance(initialBalance)
            .maxTimePerQuestionInSeconds(10)
            .questions(List.of(
                question("¿Cuánto es 3/4 + 1/4?", "1", "3/8", "1/2", "2"),
                question("¿Cuál es el perímetro de un cuadrado de lado 5?", "20", "25", "10", "15"),
                question("¿Qué es el 50% de 80?", "40", "50", "30", "60"),
                question("¿Cuánto es 7 × 8?", "56", "54", "64", "48"),
                question("¿Cuál es la raíz cuadrada de 81?", "9", "8", "7", "6")
            ))
            .build();
        return dto;
    }

    public static ClasificacionActivityRequestDto clasificacion(
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        ClasificacionActivityRequestDto dto = new ClasificacionActivityRequestDto(
            List.of(
                category("Operaciones", List.of("Suma", "Resta", "Multiplicación")),
                category("Geometría", List.of("Triángulo", "Cuadrado", "Círculo"))
            )
        );
        SimulationActivityDtoHelper.fillBase(dto, description, startDate, endDate, Difficulty.FACIL, 20, attempts, initialBalance);
        return dto;
    }

    public static AhorcadoRequestDto ahorcado(
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        AhorcadoRequestDto dto = new AhorcadoRequestDto("FRACCION", Errors.CINCO);
        SimulationActivityDtoHelper.fillBase(dto, description, startDate, endDate, Difficulty.MEDIO, 15, attempts, initialBalance);
        return dto;
    }

    private static QuestionRequestDto question(String text, String correct, String o2, String o3, String o4) {
        return QuestionRequestDto.builder()
            .question(text)
            .options(List.of(
                option(correct, true),
                option(o2, false),
                option(o3, false),
                option(o4, false)
            ))
            .build();
    }

    private static OptionRequestDto option(String text, boolean correct) {
        return OptionRequestDto.builder().option(text).isCorrect(correct).build();
    }

    private static CategoryClasificacionRequestDto category(String name, List<String> concepts) {
        return CategoryClasificacionRequestDto.builder()
            .name(name)
            .concepts(concepts.stream()
                .map(c -> ConceptClasificacionRequestDto.builder().name(c).build())
                .toList())
            .build();
    }
}
