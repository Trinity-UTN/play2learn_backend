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
import trinity.play2learn.backend.economy.wallet.mappers.WalletMapper;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.profile.profile.mappers.ProfileMapper;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.user.interfaces.IUserCreateService;

@Service
@AllArgsConstructor
public class StudentRegisterService implements IStudentRegisterService{

    private final CourseGetByIdService courseGetByIdService;

    private final IUserCreateService userCreateService;

    private final IStudentRepository studentRepository;

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

        Profile profile = Profile.builder()
        .student(studentToSave) // O podés omitirlo si no necesitás la relación inversa
        .build();

        Wallet wallet = Wallet.builder()
        .student(studentToSave)
        .balance(0.0)
        .invertedBalance(0.0)
        .build();

        studentToSave.setProfile(profile);
        studentToSave.setWallet(wallet);

        // Persistencia en cascada
        Student studentSaved = studentRepository.save(studentToSave);

        return StudentMapper.toDto(studentSaved, ProfileMapper.toDto(profile), WalletMapper.toDto(wallet));
    }
    
}
