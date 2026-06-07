package trinity.play2learn.backend.configs.seed.simulation.templates;

import java.time.LocalDateTime;
import java.util.List;

import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.CategoryClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ConceptClasificacionRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.OptionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;

/**
 * Plantillas de contenido temático para Geografía.
 */
public final class GeografiaTemplates {

    private GeografiaTemplates() {
    }

    public static final List<SimulationActivityType> SUPPORTED_TYPES = List.of(
        SimulationActivityType.ORDENAR_SECUENCIA,
        SimulationActivityType.CLASIFICACION,
        SimulationActivityType.PREGUNTADOS
    );

    public static OrdenarSecuenciaRequestDto ordenarSecuencia(
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        OrdenarSecuenciaRequestDto dto = new OrdenarSecuenciaRequestDto();
        dto.setEvents(List.of(
            event("Descubrimiento de América", "1492", 1),
            event("Revolución de Mayo", "1810", 2),
            event("Independencia Argentina", "1816", 3),
            event("Constitución Nacional", "1853", 4)
        ));
        SimulationActivityDtoHelper.fillBase(dto, description, startDate, endDate, Difficulty.MEDIO, 20, attempts, initialBalance);
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
                category("América", List.of("Brasil", "Argentina", "Chile")),
                category("Europa", List.of("España", "Francia", "Italia"))
            )
        );
        SimulationActivityDtoHelper.fillBase(dto, description, startDate, endDate, Difficulty.FACIL, 20, attempts, initialBalance);
        return dto;
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
            .difficulty(Difficulty.FACIL)
            .maxTime(25)
            .attempts(attempts)
            .initialBalance(initialBalance)
            .maxTimePerQuestionInSeconds(10)
            .questions(List.of(
                question("¿Capital de Argentina?", "Buenos Aires", "Córdoba", "Rosario", "Mendoza"),
                question("¿Río más largo del mundo?", "Nilo", "Amazonas", "Paraná", "Danubio"),
                question("¿En qué continente está Egipto?", "África", "Asia", "Europa", "Oceanía"),
                question("¿Cuál es el océano más grande?", "Pacífico", "Atlántico", "Índico", "Ártico"),
                question("¿Monte más alto del mundo?", "Everest", "Aconcagua", "K2", "Kilimanjaro")
            ))
            .build();
    }

    private static EventRequestDto event(String name, String description, int order) {
        EventRequestDto dto = new EventRequestDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setOrder(order);
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

    private static CategoryClasificacionRequestDto category(String name, List<String> concepts) {
        return CategoryClasificacionRequestDto.builder()
            .name(name)
            .concepts(concepts.stream()
                .map(c -> ConceptClasificacionRequestDto.builder().name(c).build())
                .toList())
            .build();
    }
}
