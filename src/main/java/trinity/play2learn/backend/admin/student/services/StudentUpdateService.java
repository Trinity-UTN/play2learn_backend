package trinity.play2learn.backend.admin.student.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.dtos.StudentUpdateRequestDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentExistByDNIService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentUpdateService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@Service
@AllArgsConstructor
public class StudentUpdateService implements IStudentUpdateService{

    private final IStudentGetByIdService studentGetByIdService;

    private final IStudentExistByDNIService studentExistByDNIService;

    private final ICourseGetByIdService courseGetByIdService;

    private final IStudentRepository studentRepository;

    @Override
    public StudentResponseDto cu18updateStudent(StudentUpdateRequestDto dto, Long id) {
        /**
         * Primero busco el objeto mediante el id
         * Luego actualizo los campos que no necesitan mas validaciones como los son:
         * - Name
         * - Lastname
         * 
         * Luego sigo con los campos que necesitan validaciones:
         * - DNI: Busco que no exista otro estudiante con el mismo DNI.
         * - Email: Busco que no exista un usuario con el mismo email.
         * - Course ID: Verifico que el curso exista.
         * 
         * Reemplazo los datos, guardo el objeto actualizado y retorno el objeto actualizado como StudentResponseDto.
         */
        Student student = studentGetByIdService.findById(id);

        if (!dto.getDni().equals(student.getDni()) && studentExistByDNIService.validate(dto.getDni())) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExistsByAtribute(
                    "Estudiante", 
                    "DNI", 
                    String.valueOf(dto.getDni())
                )
            );
        }

        Course course = null;
        if (student.getCourse().getId() != dto.getCourse_id()) {
            course = courseGetByIdService.findById(dto.getCourse_id());
        } else {
            course = student.getCourse();
        }

        Student updatedStudent = StudentMapper.toUpdatedEntity(student, dto, course);
         
        return StudentMapper.toDto(studentRepository.save(updatedStudent));
    }
    
}
