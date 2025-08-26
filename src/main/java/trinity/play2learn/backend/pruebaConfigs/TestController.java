package trinity.play2learn.backend.pruebaConfigs;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseRegisterService;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentRegisterService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRegisterService;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRegisterService;
import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearRegisterService;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;

@RestController
@RequestMapping("/api/test")
@AllArgsConstructor
public class TestController {

    private final IYearRepository yearRepository;

    private final ICourseRepository courseRepository;

    private final IStudentRepository studentRepository;

    private final ISubjectRegisterService subjectRegisterService;

    private final IYearRegisterService yearRegisterService;

    private final IStudentRegisterService studentRegisterService;

    private final ICourseRegisterService courseRegisterService;

    private final ITeacherRegisterService teacherRegisterService;

    private final ITeacherRepository teacherRepository;

    private final IReserveRepository reserveRepository;


    @PostMapping("/created")
    public ResponseEntity<BaseResponse<String>> testCreated() {

        Faker faker = new Faker(Locale.of("es", "AR"));

        Random random = new Random();

        Reserve reserve = Reserve.builder()
            .initialBalance(0.0)
            .circulationBalance(0.0)
            .reserveBalance(0.0)
            .build();

        reserveRepository.save(reserve);

        String[] yearsNames = new String[]{
            "Primer Año", 
            "Segundo Año", 
            "Tercer Año", 
            "Cuarto Año", 
            "Quinto Año", 
            "Sexto Año"
        };

        for (String yearName : yearsNames) {
            yearRegisterService.cu7RegisterYear(YearRequestDto.builder()
                .name(yearName)
                .build()
            );
        }
        
        Iterable <Year> years = yearRepository.findAll();

        for (Year year : years) {
            courseRegisterService.cu6RegisterCourse(CourseRequestDto.builder()
                .name("A")
                .year_id(year.getId())
                .build()
            );
            courseRegisterService.cu6RegisterCourse(CourseRequestDto.builder()
                .name("B")
                .year_id(year.getId())
                .build()
            );
            courseRegisterService.cu6RegisterCourse(CourseRequestDto.builder()
                .name("C")
                .year_id(year.getId())
                .build()
            );
        }

        Iterable<Course> courses = courseRepository.findAll();

        List<String> usedDnis = new ArrayList<>();
        List<String> usedEmails = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String name = faker.name().firstName();
            String lastname = faker.name().lastName();
            String email = generateUniqueEmail(usedEmails, faker, name, lastname);
            usedEmails.add(email);
            String dni = generateUniqueDni(usedDnis, faker);
            usedDnis.add(dni);

            teacherRegisterService.cu5RegisterTeacher(
                TeacherRequestDto.builder()
                    .name(name)
                    .lastname(lastname)
                    .email(email)
                    .dni(dni)
                    .build()
            );
        }

        Iterable<Teacher> teachers = teacherRepository.findAll();
        List<Teacher> list = new ArrayList<>();
        teachers.forEach(list::add);

        int x = 0;
        for (Course course : courses) {
            for (int i = 0; i < random.nextInt(10, 50); i++) {
                String name = faker.name().firstName();
                String lastname = faker.name().lastName();
                String email = generateUniqueEmail(usedEmails, faker, name, lastname);
                usedEmails.add(email);
                String dni = generateUniqueDni(usedDnis, faker);
                usedDnis.add(dni);
                studentRegisterService.cu4registerStudent(
                    StudentRequestDto.builder()
                        .name(name)
                        .lastname(lastname)
                        .email(email)
                        .dni(dni)
                        .course_id(course.getId())
                        .build()
                );
            }
            x++;

            SubjectRequestDto subjectDto = SubjectRequestDto.builder()
                .name("Matematica")
                .courseId(course.getId())
                .teacherId(list.get(new Random().nextInt(list.size())).getId())
                .optional(false)
                .build();
            subjectRegisterService.cu28RegisterSubject(subjectDto);
            SubjectRequestDto subjectDto2 = SubjectRequestDto.builder()
                .name("Lengua")
                .courseId(course.getId())
                .teacherId(list.get(new Random().nextInt(list.size())).getId())
                .optional(false)
                .build();
            subjectRegisterService.cu28RegisterSubject(subjectDto2);

            SubjectRequestDto subjectDto3 = SubjectRequestDto.builder()
                .name("Geografia")
                .courseId(course.getId())
                .teacherId(list.get(new Random().nextInt(list.size())).getId())
                .optional(false)
                .build();
            subjectRegisterService.cu28RegisterSubject(subjectDto3);
        }
 
        return ResponseFactory.created("Recurso creado", "Respuesta CREATED");
    }

    private String generateUniqueDni(List<String> usedDnis, Faker faker) {
        String dni;
        do {
            dni = String.valueOf(faker.number().numberBetween(20000000, 40000000));
        } while (usedDnis.contains(dni));
        usedDnis.add(dni);
        return dni;
    }

    private String generateUniqueEmail(List<String> usedEmails, Faker faker, String name, String lastname) {
        String email;
        do {
            email = name.toLowerCase() + lastname.toLowerCase() + faker.number().numberBetween(1, 1000) + "@gmail.com";
        } while (usedEmails.contains(email));
        usedEmails.add(email);
        return email;
    }

}