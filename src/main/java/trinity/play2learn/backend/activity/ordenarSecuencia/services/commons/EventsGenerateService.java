package trinity.play2learn.backend.activity.ordenarSecuencia.services.commons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.mappers.EventMapper;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IEventsGenerateService;
import trinity.play2learn.backend.configs.imgBB.dtos.ImgBBUploadResultDTO;
import trinity.play2learn.backend.configs.imgBB.services.ImageUploadService;

@Service
@AllArgsConstructor
public class EventsGenerateService implements IEventsGenerateService {

    private final ImageUploadService imageUploadService;
    
    @Override
    @Transactional
    public List<Event> generateList(List<EventRequestDto> dtos, OrdenarSecuencia ordenarSecuencia) throws IOException {
        
        List<Event> events = new ArrayList<Event>();

        for (EventRequestDto dto : dtos) {
            events.add(this.generate(dto, ordenarSecuencia));
        }

        return events;
    }

    @Override
    @Transactional
    public Event generate(EventRequestDto dto, OrdenarSecuencia ordenarSecuencia) throws IOException {

        ImgBBUploadResultDTO imagenUpload = null;

        if (dto.getImage() != null) {
            imagenUpload = imageUploadService.uploadImage(dto.getImage());
        }
        Event event = EventMapper.toModel(dto, ordenarSecuencia, (imagenUpload != null) ? imagenUpload.getImageUrl() : null);

        return event; //No guardo el evento en la BD sino que lo retorno con la actividad asignada
    }
   
}
