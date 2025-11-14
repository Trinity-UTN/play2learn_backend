package trinity.play2learn.backend.admin.subject.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SubjectTestMother {

    static Course course(Long id) {
        return Course.builder()
            .id(id)
            .name("4°B")
            .year(Year.builder().id(2024L).name("2024").build())
            .build();
    }

    static Teacher teacher(Long id) {
        return Teacher.builder()
            .id(id)
            .name("Laura")
            .lastname("Acosta")
            .dni("12345678")
            .user(User.builder()
                .id(700L + id)
                .email("teacher" + id + "@example.com")
                .role(Role.ROLE_TEACHER)
                .build())
            .build();
    }

    static Student student(Long id) {
        return Student.builder()
            .id(id)
            .name("Alumno " + id)
            .lastname("Pérez")
            .dni("DNI" + id)
            .build();
    }

    static List<Student> students(Long... ids) {
        return Arrays.stream(ids)
            .map(SubjectTestMother::student)
            .collect(Collectors.toCollection(java.util.ArrayList::new));
    }

    static Subject subject(Long id, String name, Course course, Teacher teacher, List<Student> students) {
        return Subject.builder()
            .id(id)
            .name(name)
            .course(course)
            .teacher(teacher)
            .students(students)
            .optional(true)
            .actualBalance(0.0)
            .initialBalance(0.0)
            .build();
    }
}

