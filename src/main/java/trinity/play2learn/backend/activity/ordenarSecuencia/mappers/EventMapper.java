package trinity.play2learn.backend.activity.ordenarSecuencia.mappers;

import java.util.List;
import java.util.stream.Collectors;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.EventResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;

public class EventMapper {

    public static Event toModel (EventRequestDto dto, OrdenarSecuencia ordenarSecuencia, String imageUrl) {
        return Event.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .url(imageUrl)
                .order(dto.getOrder())
                .activity(ordenarSecuencia)
                .build();
    }

    public static EventResponseDto toDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .image((event.getUrl() != null) ? event.getUrl() : null)
                .order(event.getOrder())
                .build();
    }

    public static List<EventResponseDto> toDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }
    
}
