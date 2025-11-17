package trinity.play2learn.backend.admin.teacher.mapper;

import java.util.List;

import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherSubjectsDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherUpdateDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.User;

public class TeacherMapper {

    public static Teacher toModel(TeacherRequestDto teacherDto, User user) {
        return Teacher.builder()
                .name(teacherDto.getName())
                .lastname(teacherDto.getLastname())
                .dni(teacherDto.getDni())
                .user(user)
                .build();
    }

    public static TeacherResponseDto toDto(Teacher teacher) {

        if (teacher == null)
            return null; // Esto evita un NullPointerException si se registra una materia sin docente.

        return TeacherResponseDto.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .lastname(teacher.getLastname())
                .dni(teacher.getDni())
                .user(UserMapper.toUserDto(teacher.getUser()))
                .active(teacher.getDeletedAt() == null)
                .build();
    }

    public static Teacher toUpdateModel(Long id, TeacherUpdateDto teacherDto, User user) {
        return Teacher.builder()
                .id(id)
                .name(teacherDto.getName())
                .lastname(teacherDto.getLastname())
                .dni(teacherDto.getDni())
                .user(user)
                .build();
    }

    public static List<TeacherResponseDto> toListDto(List<Teacher> teachers) {
        return teachers
            .stream()
            .map(TeacherMapper::toDto)
            .toList();
    }

    public static TeacherSubjectsDto toTeacherSubjectsDto(List<Subject> subjects, List<Course> courses, List<Year> years) {
        return TeacherSubjectsDto.builder()
            .subjects(SubjectMapper.toSimplifiedDtoList(subjects))
            .courses(CourseMapper.toListDto(courses))
            .years(YearMapper.toListDto(years))
            .build();
    }
}
