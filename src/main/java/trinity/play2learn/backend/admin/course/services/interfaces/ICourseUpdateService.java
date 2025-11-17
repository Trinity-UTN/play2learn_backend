package trinity.play2learn.backend.admin.course.services.interfaces;

import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.dtos.CourseUpdateDto;

public interface ICourseUpdateService {
    
    CourseResponseDto cu14UpdateCourse(Long id, CourseUpdateDto courseRequestDto);
}
