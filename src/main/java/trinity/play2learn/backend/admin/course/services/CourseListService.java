package trinity.play2learn.backend.admin.course.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseListService;

@Service
@AllArgsConstructor
public class CourseListService  implements ICourseListService{

    private final ICourseRepository courseRepository;
    
    @Override
    public List<CourseResponseDto> cu9ListCourses() {
        Iterable<Course> courses = courseRepository.findAllByDeletedAtIsNull();

        return CourseMapper.toListDto(courses);
    }
    
}
