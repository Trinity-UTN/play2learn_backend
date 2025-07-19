package trinity.play2learn.backend.admin.course.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetService;

@Service
@AllArgsConstructor
public class CourseGetService implements ICourseGetService {
    
    private final ICourseGetByIdService courseGetByIdService;
    
    @Override
    public CourseResponseDto cu17GetCourse(Long courseId) {
        
        return CourseMapper.toDto(courseGetByIdService.get(courseId));
        
    }
    
}
