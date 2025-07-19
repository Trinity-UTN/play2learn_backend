package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.course.models.Course;

public interface IValidateSubjectService {
    
    void subjectExistByNameAndCourse(String name , Course course);

    void subjectExistByNameAndCourseExceptId(String name , Course course, Long id);

}
