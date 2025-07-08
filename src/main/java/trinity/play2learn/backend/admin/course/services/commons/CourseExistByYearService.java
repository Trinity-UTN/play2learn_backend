package trinity.play2learn.backend.admin.course.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByYearService;
import trinity.play2learn.backend.admin.year.models.Year;

@Service
@AllArgsConstructor
public class CourseExistByYearService implements ICourseExistByYearService{

    private final ICourseRepository courseRepository;

    @Override
    public boolean validate(Year year) {
        return courseRepository.existsByYearIdAndDeletedAtIsNull(year.getId());
    }
    
}
