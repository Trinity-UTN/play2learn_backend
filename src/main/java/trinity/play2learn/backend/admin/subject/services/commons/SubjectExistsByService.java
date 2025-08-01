package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectExistsByTeacherService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@Service
@AllArgsConstructor
public class SubjectExistsByService implements  ISubjectExistsByTeacherService{
    
    private final ISubjectRepository subjectRepository;

    @Override
    public void validate(Teacher teacher) { //Valida si un profesor esta asociado a alguna materia
        if (subjectRepository.existsByTeacher(teacher)) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExists(
                    "Materia",
                    String.valueOf(teacher.getId())
                )
            );
        };
    }
}
