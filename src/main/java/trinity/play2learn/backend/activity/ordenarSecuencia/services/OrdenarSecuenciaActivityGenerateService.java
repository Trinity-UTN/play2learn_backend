package trinity.play2learn.backend.activity.ordenarSecuencia.services;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.mappers.OrdenarSecuenciaMapper;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;
import trinity.play2learn.backend.activity.ordenarSecuencia.repositories.IOrdenarSecuenciaRepository;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IEventsGenerateService;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IOrdenarSecuenciaActivityGenerateService;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IValidateEvents;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.TypeTransaccion;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.ITransaccionGenerateService;

@Service
@AllArgsConstructor
public class OrdenarSecuenciaActivityGenerateService implements IOrdenarSecuenciaActivityGenerateService{


    private final ISubjectGetByIdService subjectGetService;

    private final IValidateEvents validateEvents;

    private final IOrdenarSecuenciaRepository ordenarSecuenciaRepository;

    private final IEventsGenerateService eventsGenerateService;

    private final ITransaccionGenerateService transaccionGenerateService;
    
    @Override
    @Transactional
    public OrdenarSecuenciaResponseDto cu44GenerateOrdenarSecuencia(OrdenarSecuenciaRequestDto dto) throws IOException {
        /**
         * Que tengo que hacer:
         * - Buscar el subject por id
         * - Validar que los eventos tengan un orden correcto, no haya saltos ni repetidos
         * - Crear la actividad
         * - Crear los eventos:
         * -- En cada evento validar si tiene imagen, si tiene guardarla, si no dejarlo asi nomas
         * - Guardar la actividad
         * - Retornar el dto de respuesta con los datos de la actividad creada 
        */
        Subject subject = subjectGetService.findById(dto.getSubjectId());

        // Validar los eventos
        validateEvents.validate(dto.getEvents());

        OrdenarSecuencia ordenarSecuenciaToSave = OrdenarSecuenciaMapper.toModel(dto, subject);
    
        //Genero los modelos de eventos con la actividad asignada pero no los guardo en la BD
        List <Event> events = eventsGenerateService.generateList(dto.getEvents(), ordenarSecuenciaToSave);

        //Persisto en cascada la actividad con sus eventos
        ordenarSecuenciaToSave.setEvents(events);
        OrdenarSecuencia ordenarSecuenciaSaved = ordenarSecuenciaRepository.save(ordenarSecuenciaToSave); 

        transaccionGenerateService.generate (
            TypeTransaccion.ACTIVIDAD,
            dto.getInitialBalance(),
            "Actividad de ordenar secuencia",
            ActorTransaccion.SISTEMA,
            ActorTransaccion.SISTEMA,
            null,
            subject
        );

        return OrdenarSecuenciaMapper.toDto(ordenarSecuenciaSaved);
    }
    
}
