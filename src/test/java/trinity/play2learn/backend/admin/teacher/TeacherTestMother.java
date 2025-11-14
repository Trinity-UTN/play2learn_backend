package trinity.play2learn.backend.admin.teacher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherUpdateDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeacherTestMother {

    public static TeacherRequestDto.TeacherRequestDtoBuilder registerRequestBuilder() {
        return TeacherRequestDto.builder()
            .name("Laura")
            .lastname("Sosa")
            .email("teacher@example.com")
            .dni("12345678");
    }

    public static TeacherUpdateDto.TeacherUpdateDtoBuilder updateRequestBuilder() {
        return TeacherUpdateDto.builder()
            .name("Laura")
            .lastname("Sosa")
            .dni("87654321");
    }

    public static Teacher teacher(Long id, String dni) {
        return teacher(id, dni, teacherUser(300L, "existing.teacher@example.com"));
    }

    public static Teacher teacher(Long id, String dni, User user) {
        return Teacher.builder()
            .id(id)
            .name("Laura")
            .lastname("Sosa")
            .dni(dni)
            .user(user)
            .build();
    }

    public static Teacher deletedTeacher(Long id, String dni) {
        Teacher teacher = teacher(id, dni);
        teacher.delete();
        return teacher;
    }

    public static User teacherUser(Long id, String email) {
        return User.builder()
            .id(id)
            .email(email)
            .password("hashed-secret")
            .role(Role.ROLE_TEACHER)
            .build();
    }
}

