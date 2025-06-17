package trinity.play2learn.backend.admin.classes.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.classes.repositories.IClassRepository;
import trinity.play2learn.backend.admin.classes.services.interfaces.IClassGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.admin.classes.models.Class;

@Service
@AllArgsConstructor
public class ClassGetByIdService implements IClassGetByIdService {

    private final IClassRepository classRepository;

    /**
     * Obtiene una clase por su ID.
     *
     * @param id ID de la clase a obtener.
     * @return Clase correspondiente al ID proporcionado.
     * @throws NotFoundException si no se encuentra una clase con el ID proporcionado.
     */
    @Override
    public Class get(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Class not found with id: " + id));
    }
    
}
