package trinity.play2learn.backend.admin.course.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseListPaginatedService;
import trinity.play2learn.backend.admin.course.specs.CourseSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class CourseListPaginatedService implements ICourseListPaginatedService{

    private final ICourseRepository courseRepository;
    
    @Override
    public PaginatedData<CourseResponseDto> cu16ListPaginatedCourses(int page, int size, String orderBy,
            String orderType, String search, List<String> filters, List<String> filterValues) {        
        
        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Course> spec = Specification.where(CourseSpecs.notDeleted());
        
        if (search != null && !search.isBlank()) {
        spec = spec.and(CourseSpecs.nameContains(search));
        }
        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(CourseSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<Course> pageResult = courseRepository.findAll(spec, pageable);

        List<CourseResponseDto> dtos = CourseMapper.toListDto(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
}
