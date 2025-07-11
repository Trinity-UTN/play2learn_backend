package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.course.models.Course;

public interface IValidateSubjectService {
    
    void subjectExistByNameAndCourse(String name , Course course);

    void subjectExistById(Long id);
}
