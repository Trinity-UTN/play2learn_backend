package trinity.play2learn.backend.configs.seed.simulation.templates;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.benefits.models.BenefitCategory;
import trinity.play2learn.backend.benefits.models.BenefitColor;
import trinity.play2learn.backend.benefits.models.BenefitIcon;

/**
 * Plantillas de beneficios temáticos por materia.
 */
@Component
public class BenefitTemplateFactory {

    public BenefitTemplate pick(String subjectName, Random random) {
        return templatesFor(subjectName).get(random.nextInt(templatesFor(subjectName).size()));
    }

    public List<BenefitTemplate> templatesFor(String subjectName) {
        return switch (normalize(subjectName)) {
            case "matematica", "matemática" -> List.of(
                new BenefitTemplate("Puntos extra en parcial", "Sumá 5 puntos al próximo parcial de matemática",
                    150L, BenefitCategory.EVALUACION, BenefitIcon.EXAM, BenefitColor.BLUE),
                new BenefitTemplate("Recuperatorio express", "Acceso a recuperatorio reducido",
                    280L, BenefitCategory.EVALUACION, BenefitIcon.RETRY, BenefitColor.ORANGE),
                new BenefitTemplate("Consulta con el docente", "15 minutos de consulta personalizada",
                    120L, BenefitCategory.EXTRAS, BenefitIcon.CHAT, BenefitColor.PURPLE)
            );
            case "lengua" -> List.of(
                new BenefitTemplate("Eximición de oral", "No rendís el oral de esta unidad",
                    350L, BenefitCategory.EVALUACION, BenefitIcon.SKIP, BenefitColor.EMERALD),
                new BenefitTemplate("Corrección preferencial", "Tu trabajo se corrige primero",
                    180L, BenefitCategory.TRABAJOS, BenefitIcon.FILE, BenefitColor.AMBER),
                new BenefitTemplate("Extensión de entrega", "24 horas extra para entregar",
                    100L, BenefitCategory.TRABAJOS, BenefitIcon.CLOCK, BenefitColor.LIGHTGREEN)
            );
            case "geografia", "geografía" -> List.of(
                new BenefitTemplate("Material de estudio", "Acceso a mapas y guías exclusivas",
                    90L, BenefitCategory.EXTRAS, BenefitIcon.BOOK, BenefitColor.GRAY),
                new BenefitTemplate("Saltea una pregunta", "Omití una pregunta en la próxima evaluación",
                    220L, BenefitCategory.EVALUACION, BenefitIcon.SKIP, BenefitColor.RED),
                new BenefitTemplate("Asistencia flexible", "Una falta no penaliza",
                    160L, BenefitCategory.ASISTENCIA, BenefitIcon.CALENDAR, BenefitColor.BLUE)
            );
            default -> List.of(
                new BenefitTemplate("Beneficio general", "Beneficio de apoyo académico",
                    100L, BenefitCategory.EXTRAS, BenefitIcon.BOOK, BenefitColor.GRAY)
            );
        };
    }

    private String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase();
    }

    public record BenefitTemplate(
        String name,
        String description,
        Long baseCost,
        BenefitCategory category,
        BenefitIcon icon,
        BenefitColor color
    ) {
    }
}
