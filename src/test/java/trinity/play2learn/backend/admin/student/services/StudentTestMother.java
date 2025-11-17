package trinity.play2learn.backend.admin.student.services;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentUpdateRequestDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudentTestMother {

    public static StudentRequestDto.StudentRequestDtoBuilder registerRequestBuilder(Long courseId) {
        return StudentRequestDto.builder()
            .name("Juan")
            .lastname("Pérez")
            .email("student@example.com")
            .dni("12345678")
            .course_id(courseId)
            .emailTutor("tutor@example.com")
            .birthDate(LocalDate.of(2010, 6, 15));
    }

    public static StudentUpdateRequestDto.StudentUpdateRequestDtoBuilder updateRequestBuilder(Long courseId) {
        return StudentUpdateRequestDto.builder()
            .name("Ana María")
            .lastname("Gómez")
            .dni("87654321")
            .course_id(courseId)
            .emailTutor("tutor@school.com")
            .birthDate(LocalDate.of(2010, 5, 21));
    }

    public static Student student(Long id, String dni, Long courseId) {
        return student(id, dni, course(courseId));
    }

    public static Student student(Long id, String dni, Course course) {
        User user = studentUser(700L, "existing@student.com");
        Student student = Student.builder()
            .id(id)
            .name("Ana")
            .lastname("Gómez")
            .dni(dni)
            .course(course)
            .user(user)
            .build();
        Profile profile = Profile.builder()
            .id(900L)
            .student(student)
            .build();
        Wallet wallet = Wallet.builder()
            .id(800L)
            .student(student)
            .balance(0.0)
            .invertedBalance(0.0)
            .build();
        student.setProfile(profile);
        student.setWallet(wallet);
        return student;
    }

    public static Student studentWithEmail(Long id, String dni, Long courseId, String email) {
        Student student = student(id, dni, courseId);
        student.getUser().setEmail(email);
        return student;
    }

    public static Course course(Long id) {
        return course(id, "3ro A");
    }

    public static Course course(Long id, String name) {
        return Course.builder()
            .id(id)
            .name(name)
            .year(Year.builder()
                .id(2025L)
                .name("2025")
                .build())
            .build();
    }

    public static User studentUser(Long id, String email) {
        return User.builder()
            .id(id)
            .email(email)
            .password("hashed")
            .role(Role.ROLE_STUDENT)
            .build();
    }

    public static Student deletedStudent(Long id, String dni, Long courseId) {
        Student student = student(id, dni, courseId);
        student.delete();
        return student;
    }
}

