package trinity.play2learn.backend.admin.course.services;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.commons.CourseExistByService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseRegisterService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.services.commons.YearGetByIdService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

/**
 * Servicio para registrar una clase.
 */
@Service
@AllArgsConstructor
public class CourseRegisterService implements ICourseRegisterService {

    private final YearGetByIdService yearGetByIdService;

    private final CourseExistByService courseExistService;

    private final ICourseRepository courseRepository;

    /**
     * Registra una nueva clase en la base de datos.
     *
     * @param CourseRequestDto DTO que contiene los datos de la clase a registrar.
     * @return CourseResponseDto DTO que contiene los datos de la clase registrada.
     * @throws BadRequestException si ya existe una clase con el mismo nombre en el año seleccionado.
     * @throws BadRequestException si los datos enviados en el dto no son validos.
     */
    @Override
    public CourseResponseDto cu6RegisterCourse(CourseRequestDto courseRequestDto) {
        Year year = yearGetByIdService.findById(courseRequestDto.getYear_id());

        if (courseExistService.validate(courseRequestDto.getName(), year)){
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExists(
                    "Curso"
                )
            );
        }

        Course courseToSave = CourseMapper.toModel(courseRequestDto, year);

        return CourseMapper.toDto(courseRepository.save(courseToSave));
    }
    
}
