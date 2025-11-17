package trinity.play2learn.backend.admin.course.services.interfaces;

import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;

public interface ICourseRegisterService {
    
    public CourseResponseDto cu6RegisterCourse(CourseRequestDto courseRequestDto);
    
}
