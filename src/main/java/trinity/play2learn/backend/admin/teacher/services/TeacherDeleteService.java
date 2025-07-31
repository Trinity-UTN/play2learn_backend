package trinity.play2learn.backend.admin.teacher.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectExistsByTeacherService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherDeleteService;

@Service
@AllArgsConstructor
public class TeacherDeleteService implements ITeacherDeleteService {
    
    private final ITeacherRepository teacherRepository;
    private final ITeacherGetByIdService getTeacherByIdService;
    private final ISubjectExistsByTeacherService teacherInSubjectsService;

    @Override
    public void cu24DeleteTeacher(Long id) {

        Teacher teacher = getTeacherByIdService.findById(id); //Lanza un 404 si no encuentra un docente con el id proporcionado

        teacherInSubjectsService.validate(teacher); //Valida si un profesor esta asociado a alguna materia

        teacher.delete();

        teacherRepository.save(teacher);
    }

}
