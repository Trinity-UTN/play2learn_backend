package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ITeacherInSubjectsService {
    
    void validate(Teacher teacher); //Buscar si el estudiante esta asignado a alguna materia
}
