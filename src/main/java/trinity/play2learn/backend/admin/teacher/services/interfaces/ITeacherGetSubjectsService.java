package trinity.play2learn.backend.admin.teacher.services.interfaces;

import trinity.play2learn.backend.admin.teacher.dtos.TeacherSubjectsDto;
import trinity.play2learn.backend.user.models.User;

public interface ITeacherGetSubjectsService {
    
    TeacherSubjectsDto getSubjectsCoursesAndYears(User user);

}
