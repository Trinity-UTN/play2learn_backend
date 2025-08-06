package trinity.play2learn.backend.admin.student.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.commons.CourseGetByIdService;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentRegisterService;
import trinity.play2learn.backend.profile.profile.dtos.response.ProfileResponseDto;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileGenerateService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.user.interfaces.IUserCreateService;

@Service
@AllArgsConstructor
public class StudentRegisterService implements IStudentRegisterService{

    private final CourseGetByIdService courseGetByIdService;

    private final IUserCreateService userCreateService;

    private final IStudentRepository studentRepository;

    private final IProfileGenerateService profileGenerateService;

    @Override
    public StudentResponseDto cu4registerStudent(StudentRequestDto studentRequestDto) {
        /*
         * Primero valido los campos con el Dto
         * Luego busco si el curso existe
         * Luego busco si el usuario con este email ya existe
         * Luego creo el usuario
         * Luego creo el estudiante
         * Luego retorno el Dto de respuesta con los datos del estudiante creado
        */
        Course course = courseGetByIdService.findById(studentRequestDto.getCourse_id());

        User user = userCreateService.create(studentRequestDto.getEmail(), studentRequestDto.getDni(), Role.ROLE_STUDENT);
        
        Student studentToSave = StudentMapper.toModel(studentRequestDto, course, user);

        Student studentSaved = studentRepository.save(studentToSave);

        ProfileResponseDto profile = profileGenerateService.cu52generateProfile(studentSaved);

        return StudentMapper.toDto(studentSaved, profile);
    }
    
}
