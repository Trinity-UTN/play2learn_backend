package trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces;

import java.io.IOException;
import java.util.List;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;

public interface IEventsGenerateService {

    public List<Event> generateList (List<EventRequestDto> dtos, OrdenarSecuencia ordenarSecuencia) throws IOException;
    
    public Event generate (EventRequestDto dto, OrdenarSecuencia ordenarSecuencia) throws IOException;
}
