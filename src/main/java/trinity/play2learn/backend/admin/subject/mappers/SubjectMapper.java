package trinity.play2learn.backend.admin.subject.mappers;

import java.util.List;

import trinity.play2learn.backend.admin.course.mappers.CourseMapper;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.dtos.SubjectAddResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectUpdateRequestDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public class SubjectMapper {
    
    public static Subject toModel(SubjectRequestDto subjectDto , Course course , Teacher teacher , List<Student> students) {
        return Subject.builder()
            .name(subjectDto.getName())
            .course(course)
            .teacher(teacher)
            .students(students)
            .optional(subjectDto.getOptional())
            .actualBalance(0.0)
            .initialBalance(0.0)
            .build();
    }

    public static SubjectResponseDto toSubjectDto(Subject subject) {
        return SubjectResponseDto.builder()
            .id(subject.getId())
            .name(subject.getName())
            .course(CourseMapper.toDto(subject.getCourse()))
            .teacher(TeacherMapper.toDto(subject.getTeacher()))
            .optional(subject.getOptional())
            .actualBalance(subject.getActualBalance())
            .initialBalance(subject.getInitialBalance())
            .students(StudentMapper.toSimplificatedDtos(subject.getStudents()))
            .build();
    }

    public static Subject toUpdateModel(Long id, SubjectUpdateRequestDto subjectDto , Course course , Teacher teacher , List<Student> students) {
        return Subject.builder()
            .id(id)
            .name(subjectDto.getName())
            .course(course)
            .teacher(teacher)
            .students(students)
            .optional(subjectDto.getOptional())
            .build();
    }

    public static List<SubjectResponseDto> toDtoList(List<Subject> subjects) {
        return subjects.stream()
            .map(SubjectMapper::toSubjectDto)
            .toList();
    }

    public static SubjectAddResponseDto toAddDto(Subject subject) {
        return SubjectAddResponseDto.builder()
            .id(subject.getId())
            .subjectName(subject.getName())
            .courseName(subject.getCourse().getYear().getName() + " " + subject.getCourse().getName())
            .students(StudentMapper.toSimplificatedDtos(subject.getStudents()))
            .build();
    }
}
