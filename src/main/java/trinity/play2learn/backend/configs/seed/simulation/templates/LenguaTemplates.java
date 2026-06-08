package trinity.play2learn.backend.configs.seed.simulation.templates;

import java.time.LocalDateTime;
import java.util.List;

import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.SentenceCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.WordCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.OptionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;

/**
 * Plantillas de contenido temático para Lengua.
 */
public final class LenguaTemplates {

    private LenguaTemplates() {
    }

    public static final List<SimulationActivityType> SUPPORTED_TYPES = List.of(
        SimulationActivityType.COMPLETAR_ORACION,
        SimulationActivityType.PREGUNTADOS,
        SimulationActivityType.AHORCADO
    );

    public static CompletarOracionActivityRequestDto completarOracion(
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        return CompletarOracionActivityRequestDto.builder()
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .difficulty(Difficulty.FACIL)
            .maxTime(20)
            .attempts(attempts)
            .initialBalance(initialBalance)
            .sentences(List.of(
                sentence(List.of(
                    word("El", 1, false),
                    word("gato", 2, true),
                    word("duerme", 3, false)
                )),
                sentence(List.of(
                    word("La", 1, false),
                    word("literatura", 2, true),
                    word("es", 3, false),
                    word("arte", 4, false)
                ))
            ))
            .build();
    }

    public static PreguntadosRequestDto preguntados(
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        return PreguntadosRequestDto.builder()
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .difficulty(Difficulty.MEDIO)
            .maxTime(25)
            .attempts(attempts)
            .initialBalance(initialBalance)
            .maxTimePerQuestionInSeconds(10)
            .questions(List.of(
                question("¿Qué es un sustantivo?", "Palabra que nombra", "Palabra que acciona", "Palabra que describe", "Palabra que une"),
                question("¿Cuál es un sinónimo de 'rápido'?", "Veloz", "Lento", "Pesado", "Triste"),
                question("¿Qué signo cierra una pregunta?", "?", ".", ",", "!"),
                question("¿Qué es un verbo?", "Palabra que indica acción", "Palabra que nombra", "Palabra que modifica", "Palabra que une"),
                question("¿Cuántas vocales tiene el español?", "5", "4", "6", "7")
            ))
            .build();
    }

    public static AhorcadoRequestDto ahorcado(
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        AhorcadoRequestDto dto = new AhorcadoRequestDto("SINTAXIS", Errors.TRES);
        SimulationActivityDtoHelper.fillBase(dto, description, startDate, endDate, Difficulty.MEDIO, 15, attempts, initialBalance);
        return dto;
    }

    private static QuestionRequestDto question(String text, String correct, String o2, String o3, String o4) {
        return QuestionRequestDto.builder()
            .question(text)
            .options(List.of(
                OptionRequestDto.builder().option(correct).isCorrect(true).build(),
                OptionRequestDto.builder().option(o2).isCorrect(false).build(),
                OptionRequestDto.builder().option(o3).isCorrect(false).build(),
                OptionRequestDto.builder().option(o4).isCorrect(false).build()
            ))
            .build();
    }

    private static SentenceCompletarOracionRequestDto sentence(List<WordCompletarOracionRequestDto> words) {
        return SentenceCompletarOracionRequestDto.builder().words(words).build();
    }

    private static WordCompletarOracionRequestDto word(String text, int order, boolean missing) {
        return WordCompletarOracionRequestDto.builder()
            .word(text)
            .wordOrder(order)
            .isMissing(missing)
            .build();
    }
}
