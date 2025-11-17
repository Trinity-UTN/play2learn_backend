package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.course.models.Course;

public interface ISubjectExistsByNameAndCourseService {
    
    void existByNameAndCourse(String name , Course course);

    void existByNameAndCourseAndIdNot(String name , Course course, Long id);

}
