package trinity.play2learn.backend.admin.student.mappers;

import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.User;

import trinity.play2learn.backend.admin.classes.mappers.ClassMapper;
import trinity.play2learn.backend.admin.classes.models.Class;

public class StudentMapper {
    
    public static Student toModel(StudentRequestDto studentDto, Class classes, User user) {
        return Student.builder()
            .name(studentDto.getName())
            .lastname(studentDto.getLastname())
            .dni(studentDto.getDni())
            .user(user)
            .classes(classes)
            .build();
    }

    public static StudentResponseDto toDto(Student student) {
        return StudentResponseDto.builder()
            .id(student.getId())
            .name(student.getName())
            .lastname(student.getLastname())
            .dni(student.getDni())
            .user(UserMapper.toUserDto(student.getUser()))
            .classes(ClassMapper.toDto(student.getClasses()))
            .build();
    }
}
