package trinity.play2learn.backend.admin.course.services.interfaces;

import trinity.play2learn.backend.admin.course.models.Course;

public interface ICourseGetByIdService {
    
    public Course get(Long id);
}
