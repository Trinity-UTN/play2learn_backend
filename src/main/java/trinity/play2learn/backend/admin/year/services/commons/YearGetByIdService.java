package trinity.play2learn.backend.admin.year.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;

@Service
@AllArgsConstructor
public class YearGetByIdService implements IYearGetByIdService{

    private IYearRepository yearRepository;
    /**
     * Recibe un id y devuelve el año correspondiente.
     *
     * @param Long id
     * @return Year
     * @throws NotFoundException si no se encuentra el año con el id proporcionado.
     */
    @Override
    public Year findById(Long id) {
        return yearRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundExceptionMesagges.resourceNotFoundById("Año", String.valueOf(id))));
    }
    
}
