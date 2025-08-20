package trinity.play2learn.backend.pruebaConfigs;


import java.util.Locale;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRegisterService;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

@RestController
@RequestMapping("/api/test")
@AllArgsConstructor
public class TestController {

    private final ICourseRepository courseRepository;

    private final IStudentRepository studentRepository;

    private final ISubjectRegisterService subjectRegisterService;


    @PostMapping("/created")
    public ResponseEntity<BaseResponse<String>> testCreated() {

        Faker faker = new Faker(Locale.of("es", "AR"));

        Random random = new Random();

        Iterable<Course> courses = courseRepository.findAll();

        for (Course course : courses) {

            SubjectRequestDto subjectDto = SubjectRequestDto.builder()
                .name("Matematica")
                .courseId(course.getId())
                .teacherId(random.nextLong(1,11))
                .optional(false)
                .build();
            subjectRegisterService.cu28RegisterSubject(subjectDto);
            SubjectRequestDto subjectDto2 = SubjectRequestDto.builder()
                .name("Lengua")
                .courseId(course.getId())
                .teacherId(random.nextLong(1,11))
                .optional(false)
                .build();
            subjectRegisterService.cu28RegisterSubject(subjectDto2);

            SubjectRequestDto subjectDto3 = SubjectRequestDto.builder()
                .name("Geografia")
                .courseId(course.getId())
                .teacherId(random.nextLong(1,11))
                .optional(false)
                .build();
            subjectRegisterService.cu28RegisterSubject(subjectDto3);

        }

           
        return ResponseFactory.created("Recurso creado", "Respuesta CREATED");
    }

}