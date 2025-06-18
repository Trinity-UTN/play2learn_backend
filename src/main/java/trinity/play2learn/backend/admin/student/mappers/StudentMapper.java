package trinity.play2learn.backend.admin.student.mappers;

import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
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
            .build();
    }
}
