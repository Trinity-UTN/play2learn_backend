package trinity.play2learn.backend.activity.ordenarSecuencia.services.commons;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IValidateEvents;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.messages.BadRequestExceptionMessages;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Service
public class ValidateEvents implements IValidateEvents{

    @Override
    public void validate(List<EventRequestDto> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            throw new BadRequestException(
                ValidationMessages.NOT_EMPTY_EVENT_LIST
            );
        }
        // Extraer todos los números de orden en un Set para detectar repeticiones
        Set<Integer> ordenes = new HashSet<>();
        for (EventRequestDto evento : eventos) {
            Integer orden = evento.getOrder();
            if (orden == null || orden < 0) {
                throw new BadRequestException(
                    ValidationMessages.MIN_ORDER
                );
            }
            if (!ordenes.add(orden)) {
                // Si add devuelve false, el número ya estaba en el set (repetido)
                throw new BadRequestException(
                    BadRequestExceptionMessages.eventOrderRepeated(String.valueOf(orden))
                );
            }
        }

        // Validar que no haya saltos
        int maxOrden = Collections.max(ordenes);
        if (maxOrden != ordenes.size()-1) {
            throw new BadRequestException(
                BadRequestExceptionMessages.eventOrderNotConsecutive(String.valueOf(maxOrden)) 
            );
        }
    }
    
}
