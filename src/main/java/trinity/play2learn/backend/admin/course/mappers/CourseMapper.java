package trinity.play2learn.backend.admin.course.mappers;

import java.util.ArrayList;
import java.util.List;

import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;

public class CourseMapper {
    /**
     * Transforma un DTO de clase en un modelo de clase.
     *
     * @param CourseRequestDto classDto
     * @param Year year
     * @return Class
     */
    public static Course toModel(CourseRequestDto courseDto, Year year) {
        return Course.builder()
            .name(courseDto.getName())
            .year(year)
            .build();
    }
    /**
     * Transforma un modelo de clase en un DTO de respuesta de curso.
     *
     * @param Course courseModel
     * @return CourseResponseDto
     */
    public static CourseResponseDto toDto(Course courseModel) {
        return CourseResponseDto.builder()
            .id(courseModel.getId())
            .name(courseModel.getName())
            .year(YearMapper.toDto(courseModel.getYear()))
            .build();
    }

    public static List<CourseResponseDto> toListDto(Iterable<Course> courses) {
        List<CourseResponseDto> courseDtos = new ArrayList<>();
        for (Course course : courses) {
            courseDtos.add(toDto(course));
        }
        return courseDtos;
    } 
}
