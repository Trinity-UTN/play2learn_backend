package trinity.play2learn.backend.admin.course.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.dtos.CourseUpdateDto;
import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseUpdateService;

@Service
@AllArgsConstructor
public class CourseUpdateService implements ICourseUpdateService {
    
    private final ICourseRepository courseRepository;
    private final ICourseExistByService courseExistService;
    private final ICourseGetByIdService courseGetByIdService;

    @Override
    public CourseResponseDto cu14UpdateCourse(Long id , CourseUpdateDto courseDto) {

        Course course = courseGetByIdService.findById(id); //Lanza un 404 si no se encuentra un curso con el ID proporcionado

        courseExistService.validateExceptId(id, courseDto.getName(), course.getYear()); //Lanza un 409 si ya existe un curso con el mismo nombre en el mismo año

        return CourseMapper.toDto(courseRepository.save(CourseMapper.toUpdateModel(id, courseDto, course.getYear())));
    }


}
