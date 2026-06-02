package trinity.play2learn.backend.user.services.user;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.user.interfaces.IUserRestorePasswordService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class UserRestorePasswordService implements IUserRestorePasswordService {

    private final IStudentGetByIdService studentGetByIdService;

    private final ITeacherGetByIdService teacherGetByIdService;

    private final IUserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void cu114RestoreStudentPassword(Long id) {

        Student student = studentGetByIdService.findById(id);

        student.getUser().setPassword(passwordEncoder.encode(student.getDni()));

        userRepository.save(student.getUser());
    }

    @Override
    public void cu115RestoreTeacherPassword(Long id) {
        Teacher teacher = teacherGetByIdService.findById(id);

        teacher.getUser().setPassword(passwordEncoder.encode(teacher.getDni()));

        userRepository.save(teacher.getUser());
    }
}
