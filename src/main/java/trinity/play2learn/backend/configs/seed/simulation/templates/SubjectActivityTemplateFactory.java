package trinity.play2learn.backend.configs.seed.simulation.templates;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto;
import trinity.play2learn.backend.admin.subject.models.Subject;

/**
 * Selecciona plantillas de actividad acordes al nombre de la materia.
 */
@Component
public class SubjectActivityTemplateFactory {

    public SimulationActivitySpec createSpec(
        Subject subject,
        SimulationActivityType type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance,
        Random random
    ) {
        String subjectName = normalize(subject.getName());
        String description = buildDescription(subjectName, type, random);
        ActivityRequestDto dto = buildDto(subjectName, type, description, startDate, endDate, attempts, initialBalance);
        return new SimulationActivitySpec(type, description, dto);
    }

    public SimulationActivityType pickRandomType(String subjectName, Random random) {
        List<SimulationActivityType> types = typesFor(normalize(subjectName));
        return types.get(random.nextInt(types.size()));
    }

    public List<SimulationActivityType> typesFor(String subjectName) {
        return switch (normalize(subjectName)) {
            case "matematica", "matemática" -> MatematicaTemplates.SUPPORTED_TYPES;
            case "lengua" -> LenguaTemplates.SUPPORTED_TYPES;
            case "geografia", "geografía" -> GeografiaTemplates.SUPPORTED_TYPES;
            default -> MatematicaTemplates.SUPPORTED_TYPES;
        };
    }

    private ActivityRequestDto buildDto(
        String subjectName,
        SimulationActivityType type,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        return switch (normalize(subjectName)) {
            case "matematica", "matemática" -> matematica(type, description, startDate, endDate, attempts, initialBalance);
            case "lengua" -> lengua(type, description, startDate, endDate, attempts, initialBalance);
            case "geografia", "geografía" -> geografia(type, description, startDate, endDate, attempts, initialBalance);
            default -> matematica(type, description, startDate, endDate, attempts, initialBalance);
        };
    }

    private ActivityRequestDto matematica(
        SimulationActivityType type,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        return switch (type) {
            case PREGUNTADOS -> MatematicaTemplates.preguntados(description, startDate, endDate, attempts, initialBalance);
            case CLASIFICACION -> MatematicaTemplates.clasificacion(description, startDate, endDate, attempts, initialBalance);
            case AHORCADO -> MatematicaTemplates.ahorcado(description, startDate, endDate, attempts, initialBalance);
            default -> MatematicaTemplates.preguntados(description, startDate, endDate, attempts, initialBalance);
        };
    }

    private ActivityRequestDto lengua(
        SimulationActivityType type,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        return switch (type) {
            case COMPLETAR_ORACION -> LenguaTemplates.completarOracion(description, startDate, endDate, attempts, initialBalance);
            case PREGUNTADOS -> LenguaTemplates.preguntados(description, startDate, endDate, attempts, initialBalance);
            case AHORCADO -> LenguaTemplates.ahorcado(description, startDate, endDate, attempts, initialBalance);
            default -> LenguaTemplates.preguntados(description, startDate, endDate, attempts, initialBalance);
        };
    }

    private ActivityRequestDto geografia(
        SimulationActivityType type,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int attempts,
        double initialBalance
    ) {
        return switch (type) {
            case ORDENAR_SECUENCIA -> GeografiaTemplates.ordenarSecuencia(description, startDate, endDate, attempts, initialBalance);
            case CLASIFICACION -> GeografiaTemplates.clasificacion(description, startDate, endDate, attempts, initialBalance);
            case PREGUNTADOS -> GeografiaTemplates.preguntados(description, startDate, endDate, attempts, initialBalance);
            default -> GeografiaTemplates.preguntados(description, startDate, endDate, attempts, initialBalance);
        };
    }

    private String buildDescription(String subjectName, SimulationActivityType type, Random random) {
        List<String> variants = switch (type) {
            case PREGUNTADOS -> List.of("Repaso de " + subjectName, "Quiz de " + subjectName);
            case AHORCADO -> List.of("Ahorcado de " + subjectName, "Vocabulario de " + subjectName);
            case CLASIFICACION -> List.of("Clasificación de " + subjectName, "Conceptos de " + subjectName);
            case COMPLETAR_ORACION -> List.of("Completar oraciones de " + subjectName);
            case ORDENAR_SECUENCIA -> List.of("Línea de tiempo de " + subjectName, "Secuencia histórica de " + subjectName);
        };
        return variants.get(random.nextInt(variants.size()));
    }

    private String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase();
    }

    public record SimulationActivitySpec(
        SimulationActivityType type,
        String description,
        ActivityRequestDto requestDto
    ) {
    }
}
