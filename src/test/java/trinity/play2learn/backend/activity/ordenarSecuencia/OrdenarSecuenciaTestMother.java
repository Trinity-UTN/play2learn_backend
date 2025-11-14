package trinity.play2learn.backend.activity.ordenarSecuencia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.EventResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;
import trinity.play2learn.backend.admin.subject.models.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrdenarSecuenciaTestMother {

    public static final String DEFAULT_EVENT_NAME = "Evento";
    public static final String DEFAULT_EVENT_DESCRIPTION = "Descripción del evento";
    public static final String MAX_LENGTH_EVENT_NAME = "a".repeat(50);
    public static final String MAX_LENGTH_EVENT_DESCRIPTION = "a".repeat(100);
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static OrdenarSecuenciaRequestDto ordenarSecuenciaRequestDto(
        List<EventRequestDto> events
    ) {
        LocalDateTime now = LocalDateTime.now();
        OrdenarSecuenciaRequestDto dto = OrdenarSecuenciaRequestDto.builder()
            .events(events != null ? events : List.of())
            .build();
        dto.setDescription("Descripción de la actividad de ordenar secuencia");
        dto.setStartDate(now.plusDays(1));
        dto.setEndDate(now.plusDays(7));
        dto.setDifficulty(Difficulty.FACIL);
        dto.setMaxTime(30);
        dto.setAttempts(3);
        dto.setSubjectId(ActivityTestMother.SUBJECT_ID);
        dto.setInitialBalance(DEFAULT_INITIAL_BALANCE);
        return dto;
    }

    public static OrdenarSecuenciaRequestDto validOrdenarSecuenciaRequestDto() {
        return ordenarSecuenciaRequestDto(
            events(3)
        );
    }

    public static EventRequestDto event(String name, String description, Integer order) {
        EventRequestDto event = new EventRequestDto();
        event.setName(name != null ? name : DEFAULT_EVENT_NAME);
        event.setDescription(description != null ? description : DEFAULT_EVENT_DESCRIPTION);
        event.setOrder(order != null ? order : 0);
        return event;
    }

    public static EventRequestDto event(Integer order) {
        return event("Evento " + (order + 1), "Descripción del evento " + (order + 1), order);
    }

    public static List<EventRequestDto> events(int count) {
        List<EventRequestDto> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            events.add(event(i));
        }
        return events;
    }

    public static List<EventRequestDto> eventsWithInvalidOrder(int count) {
        List<EventRequestDto> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            EventRequestDto event = event(i);
            event.setOrder(i + 1); // Orden inválido: empieza en 1 en lugar de 0, o salta números
            events.add(event);
        }
        return events;
    }

    public static OrdenarSecuenciaResponseDto ordenarSecuenciaResponseDto(
        Long id,
        List<EventResponseDto> events
    ) {
        return OrdenarSecuenciaResponseDto.builder()
            .id(id)
            .name("Ordenar Secuencia")
            .description("Descripción de la actividad de ordenar secuencia")
            .events(events != null ? events : List.of())
            .build();
    }

    public static OrdenarSecuenciaResponseDto validOrdenarSecuenciaResponseDto(Long id) {
        return ordenarSecuenciaResponseDto(id, List.of());
    }

    public static OrdenarSecuencia savedOrdenarSecuencia(Long id, Subject subject) {
        return OrdenarSecuencia.builder()
            .id(id)
            .name("Ordenar Secuencia")
            .description("Descripción de la actividad de ordenar secuencia")
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .events(List.of())
            .build();
    }

    public static List<Event> savedEvents(OrdenarSecuencia ordenarSecuencia, int count) {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            events.add(Event.builder()
                .id((long) (i + 1))
                .name("Evento " + (i + 1))
                .description("Descripción del evento " + (i + 1))
                .order(i)
                .activity(ordenarSecuencia)
                .build());
        }
        return events;
    }
}

