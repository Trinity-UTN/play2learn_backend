package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ISubjectExistsByTeacherService {
    
    void validate(Teacher teacher); //Buscar si el estudiante esta asignado a alguna materia
}
