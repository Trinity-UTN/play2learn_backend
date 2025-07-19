package trinity.play2learn.backend.admin.course.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseDeleteService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsExistByCourseService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectsExistsByCourseService;

@Service
@AllArgsConstructor
public class CourseDeleteService implements ICourseDeleteService{
    
    private final ICourseRepository courseRepository;
    private final ICourseGetByIdService courseGetByIdService;
    private final IStudentsExistByCourseService studentsExistByCourseService;
    private final ISubjectsExistsByCourseService subjectsExistsByCourseService;

    @Override
    public void deleteCourse(Long id) {

        Course course = courseGetByIdService.get(id);

        studentsExistByCourseService.validate(course.getId()); //Lanza un 409 si hay estudiantes asociados al curso

        subjectsExistsByCourseService.validate(course); //Lanza un 409 si hay materias asociadas al curso

        course.delete();
        courseRepository.save(course);
    }
}
