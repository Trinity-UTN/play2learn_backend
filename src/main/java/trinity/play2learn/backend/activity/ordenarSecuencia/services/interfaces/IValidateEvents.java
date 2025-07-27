package trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;

public interface IValidateEvents {
    
    public void validate (List<EventRequestDto> events);
    
}
