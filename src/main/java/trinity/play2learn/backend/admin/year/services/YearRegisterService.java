package trinity.play2learn.backend.admin.year.services;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearExistService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearRegisterService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

/**
 * Servicio para la lógica de negocio relacionada con el caso de uso 7, registrar un año académico.
 * Encapsula validaciones y operaciones sobre el modelo Year.
 */
@Service
@AllArgsConstructor
public class YearRegisterService implements IYearRegisterService {

    private final IYearExistService  yearExistService;
    private final IYearRepository yearRepository;

    /**
     * Crea un nuevo año si no existe uno con el mismo nombre.
     *
     * @param YearRequestDto.
     * @return YearResponseDto.
     * @throws BadRequestException si ya existe un año con ese nombre.
     * @throws BadRequestException si el nombre tiene caracteres especiales.
     * @throws BadRequestException si el nombre supera los 50 caracteres.
     * @throws BadRequestException si el nombre es vacio.
     */
    @Override
    public YearResponseDto cu7RegisterYear (YearRequestDto yearRequestDto) {
        /*if (result.hasFieldErrors()) {
            throw new BadRequestException("An error ocurred.", result.getFieldErrors().stream().map(err -> err.getField() + ": " + err.getDefaultMessage()).toList());
        }*/
        if (yearExistService.validate(yearRequestDto.getName())) {
            throw new BadRequestException("Year already exists.");
        }
        Year yearToSave = YearMapper.toModel(yearRequestDto);
        return YearMapper.toDto(yearRepository.save(yearToSave));
    }
    
}
