package trinity.play2learn.backend.admin.year.services;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.dtos.YearUpdateRequestDto;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearExistService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearGetByIdService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearUpdateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

@Service
@AllArgsConstructor
public class YearUpdateService implements IYearUpdateService {

    private final IYearExistService yearExistService;

    private final IYearGetByIdService yearGetByIdService;

    private final IYearRepository yearRepository;


    /**
     * Actualiza un año existente en la base de datos.
     *
     * @param yearUpdateRequestDto DTO que contiene los datos del año a actualizar
     * @return YearResponseDto DTO con los datos del año actualizado
     * @throws BadRequestException si ya existe un año con el mismo nombre
     * @throws BadRequestException si los datos enviados en el dto no son validos
     */
    @Override
    public YearResponseDto cu10UpdateYear(Long id , YearUpdateRequestDto yearUpdateRequestDto) {
        
        Year yearToUpdate = yearGetByIdService.get(id);

        yearExistService.validateExceptId(yearUpdateRequestDto.getName(), id); //Lanza un 409 si ya existe un año con el mismo nombre y no es el del id

        yearToUpdate.setName(yearUpdateRequestDto.getName());

        return YearMapper.toDto(yearRepository.save(yearToUpdate));
    }
    
}
