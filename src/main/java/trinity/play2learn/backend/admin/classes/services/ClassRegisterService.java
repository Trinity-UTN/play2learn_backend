package trinity.play2learn.backend.admin.classes.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.classes.models.Class;
import trinity.play2learn.backend.admin.classes.repositories.IClassRepository;
import trinity.play2learn.backend.admin.classes.dtos.ClassRequestDto;
import trinity.play2learn.backend.admin.classes.dtos.ClassResponseDto;
import trinity.play2learn.backend.admin.classes.mappers.ClassMapper;
import trinity.play2learn.backend.admin.classes.services.commons.ClassExistService;
import trinity.play2learn.backend.admin.classes.services.interfaces.IClassRegisterService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.services.commons.YearGetByIdService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

/**
 * Servicio para registrar una clase.
 */
@Service
@AllArgsConstructor
public class ClassRegisterService implements IClassRegisterService {

    private final YearGetByIdService yearGetByIdService;

    private final ClassExistService classExistService;

    private final IClassRepository classRepository;

    /**
     * Registra una nueva clase en la base de datos.
     *
     * @param ClassRequestDto DTO que contiene los datos de la clase a registrar.
     * @return ClassResponseDto DTO que contiene los datos de la clase registrada.
     * @throws BadRequestException si ya existe una clase con el mismo nombre en el año seleccionado.
     * @throws BadRequestException si los datos enviados en el dto no son validos.
     */
    @Override
    public ClassResponseDto cu6RegisterClass(ClassRequestDto classRequestDto) {
        Year year = yearGetByIdService.get(classRequestDto.getYear_id());

        if (classExistService.validate(classRequestDto.getName(), year)){
            throw new BadRequestException("A class with the same name already exists in the selected year.");
        }

        Class classToSave = ClassMapper.toModel(classRequestDto, year);

        return ClassMapper.toDto(classRepository.save(classToSave));
    }
    
}
