package trinity.play2learn.backend.admin.student.mappers;

import java.util.List;

import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.dtos.StudentSimplificatedResponse;
import trinity.play2learn.backend.admin.student.dtos.StudentUpdateRequestDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.User;

public class StudentMapper {
    
    public static Student toModel(StudentRequestDto studentDto, Course course, User user) {
        return Student.builder()
            .name(studentDto.getName())
            .lastname(studentDto.getLastname())
            .dni(studentDto.getDni())
            .user(user)
            .course(course)
            .build();
    }

    public static StudentResponseDto toDto(Student student) {
        return StudentResponseDto.builder()
            .id(student.getId())
            .name(student.getName())
            .lastname(student.getLastname())
            .dni(student.getDni())
            .user(UserMapper.toUserDto(student.getUser()))
            .course(CourseMapper.toDto(student.getCourse()))
            .active(student.getDeletedAt() == null)
            .build();
    }

    public static Student toUpdatedEntity (Student model, StudentUpdateRequestDto dto, Course course) {
        return Student.builder()
            .id(model.getId())
            .name(dto.getName())
            .lastname(dto.getLastname())
            .dni(dto.getDni())
            .course(course)
            .user(model.getUser())
            .deletedAt(model.getDeletedAt())
            .build();
    }

    public static StudentSimplificatedResponse toSimplificatedDto (Student student) {
        return StudentSimplificatedResponse.builder()
            .id(student.getId())
            .name(student.getName())
            .lastname(student.getLastname())
            .dni(student.getDni())
            .build();
    }

    public static List<StudentSimplificatedResponse> toSimplificatedDtos (List<Student> students) {
        return students
            .stream()
            .map(student -> toSimplificatedDto(student))
            .toList();
    }

    public static List<StudentResponseDto> toListDto (List<Student> students) {
        return students.stream()
            .map(StudentMapper::toDto)
            .toList();
    }
}
