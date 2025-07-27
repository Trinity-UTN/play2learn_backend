package trinity.play2learn.backend.activity.ordenarSecuencia.services.commons;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IValidateEvents;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

@Service
public class ValidateEvents implements IValidateEvents{

    @Override
    public void validate(List<EventRequestDto> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            throw new BadRequestException("La lista de eventos no puede estar vacía");
        }
        // Extraer todos los números de orden en un Set para detectar repeticiones
        Set<Integer> ordenes = new HashSet<>();
        for (EventRequestDto evento : eventos) {
            Integer orden = evento.getOrder();
            if (orden == null || orden < 1) {
                throw new BadRequestException("El orden de cada evento debe ser un número entero mayor o igual a 1");
            }
            if (!ordenes.add(orden)) {
                // Si add devuelve false, el número ya estaba en el set (repetido)
                throw new BadRequestException("El orden del evento está repetido: " + orden);
            }
        }

        // Validar que no haya saltos
        int maxOrden = Collections.max(ordenes);
        if (maxOrden != ordenes.size()) {
            throw new BadRequestException("El orden de eventos no es consecutivo. Se esperaban " + maxOrden + " eventos consecutivos sin saltos.");
        }
    }
    
}
