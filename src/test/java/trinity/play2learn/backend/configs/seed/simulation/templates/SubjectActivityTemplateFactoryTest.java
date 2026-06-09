package trinity.play2learn.backend.configs.seed.simulation.templates;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto;
import trinity.play2learn.backend.admin.subject.models.Subject;

class SubjectActivityTemplateFactoryTest {

    private SubjectActivityTemplateFactory factory;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        factory = new SubjectActivityTemplateFactory();
        start = LocalDateTime.of(2025, 2, 1, 10, 0);
        end = LocalDateTime.of(2025, 2, 28, 18, 0);
    }

    @Test
    @DisplayName("Given Matematica subject When creating spec Then returns non-empty DTO")
    void matematica_returnsValidSpec() {
        Subject subject = Subject.builder().name("Matemática").build();
        SimulationActivityType type = SimulationActivityType.PREGUNTADOS;

        SubjectActivityTemplateFactory.SimulationActivitySpec spec = factory.createSpec(
            subject, type, start, end, 3, 100.0, new Random(42)
        );

        assertThat(spec.description()).isNotBlank();
        assertThat(spec.requestDto()).isNotNull();
        assertThat(spec.requestDto().getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("Given each default subject When listing types Then returns supported types")
    void defaultSubjects_haveTypes() {
        assertThat(factory.typesFor("Matemática")).isNotEmpty();
        assertThat(factory.typesFor("Lengua")).isNotEmpty();
        assertThat(factory.typesFor("Geografía")).isNotEmpty();
    }

    @Test
    @DisplayName("Given subject When picking random type Then type is compatible")
    void pickRandomType_isCompatible() {
        Subject subject = Subject.builder().name("Lengua").build();
        SimulationActivityType type = factory.pickRandomType(subject.getName(), new Random(1));
        assertThat(factory.typesFor("Lengua")).contains(type);
    }

    @Test
    @DisplayName("Given Geografia ordenar secuencia When creating Then has events")
    void geografiaOrdenarSecuencia_hasEvents() {
        Subject subject = Subject.builder().name("Geografía").build();
        SubjectActivityTemplateFactory.SimulationActivitySpec spec = factory.createSpec(
            subject,
            SimulationActivityType.ORDENAR_SECUENCIA,
            start,
            end,
            3,
            120.0,
            new Random(7)
        );

        ActivityRequestDto dto = spec.requestDto();
        assertThat(dto.getStartDate()).isEqualTo(start);
        assertThat(dto.getEndDate()).isEqualTo(end);
    }
}
