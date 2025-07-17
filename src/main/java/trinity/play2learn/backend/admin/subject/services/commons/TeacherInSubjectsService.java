package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ITeacherInSubjectsService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class TeacherInSubjectsService implements  ITeacherInSubjectsService{
    
    private final ISubjectRepository subjectRepository;

    @Override
    public void validate(Teacher teacher) { //Valida si un profesor esta asociado a alguna materia
        if (subjectRepository.existsByTeacher(teacher)) {
            throw new ConflictException("Teacher with ID " + teacher.getId() + " is associated with a subject.");
        };
    }
}
