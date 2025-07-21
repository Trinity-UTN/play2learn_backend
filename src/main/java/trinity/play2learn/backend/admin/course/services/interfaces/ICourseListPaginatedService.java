package trinity.play2learn.backend.admin.course.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.configs.response.PaginatedData;

public interface ICourseListPaginatedService {

    public PaginatedData<CourseResponseDto> cu16ListPaginatedCourses(
        int page, int size, String orderBy, String orderType,
        String search, List<String> filters, List<String> filterValues
    );
    
}
