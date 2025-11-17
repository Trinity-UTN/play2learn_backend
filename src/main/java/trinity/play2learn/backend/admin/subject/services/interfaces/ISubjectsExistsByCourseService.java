package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.course.models.Course;

public interface ISubjectsExistsByCourseService {
    
    void validate(Course course);
}
