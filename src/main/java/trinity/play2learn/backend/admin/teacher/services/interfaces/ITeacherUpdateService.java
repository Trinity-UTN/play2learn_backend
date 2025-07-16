package trinity.play2learn.backend.admin.teacher.services.interfaces;

import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherUpdateDto;

public interface ITeacherUpdateService {
    TeacherResponseDto cu23UpdateTeacher(Long id, TeacherUpdateDto teacher);
}
