package trinity.play2learn.backend.configs.seed.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseRegisterService;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentRegisterService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRefillBalanceService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRegisterService;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRegisterService;
import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearRegisterService;
import trinity.play2learn.backend.configs.seed.config.DatabaseSeedProperties;
import trinity.play2learn.backend.configs.seed.dtos.SeedCredentialsDto;
import trinity.play2learn.backend.configs.seed.dtos.SeedResultDto;
import trinity.play2learn.backend.configs.seed.services.interfaces.IDatabaseSeedService;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.user.dtos.signUp.SignUpRequestDto;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.services.signUp.interfaces.ISignUpService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserExistService;

/**
 * Orquesta el repoblado de la base de datos con datos mínimos operativos para desarrollo.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@AllArgsConstructor
public class DatabaseSeedService implements IDatabaseSeedService {

    private final DatabaseSeedProperties properties;
    private final IReserveRepository reserveRepository;
    private final IReserveFindLastService reserveFindLastService;
    private final ISignUpService signUpService;
    private final IUserExistService userExistService;
    private final IYearRegisterService yearRegisterService;
    private final IYearRepository yearRepository;
    private final ICourseRegisterService courseRegisterService;
    private final ICourseRepository courseRepository;
    private final ITeacherRegisterService teacherRegisterService;
    private final ITeacherRepository teacherRepository;
    private final IStudentRegisterService studentRegisterService;
    private final IStudentRepository studentRepository;
    private final ISubjectRegisterService subjectRegisterService;
    private final ISubjectRefillBalanceService subjectRefillBalanceService;
    private final IWalletAddAmountService walletAddAmountService;
    private final AspectSeedService aspectSeedService;

    private final IProfileRepository profileRepository;

    private final CredentialsMarkdownWriter credentialsMarkdownWriter;

    @Override
    public SeedResultDto execute() {
        SeedDataCollector collector = new SeedDataCollector();

        seedReserve(collector);
        seedSystemUsers(collector);
        seedAcademicStructure(collector);
        seedEconomy(collector);
        seedAspectsAndInventory(collector);

        String credentialsPath;
        try {
            credentialsPath = credentialsMarkdownWriter.write(collector.getCredentials(), collector.toCounts());
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el archivo de credenciales", e);
        }

        log.info("Seed completado: {}", collector.toCounts());

        return SeedResultDto.builder()
            .message("Seed de base de datos completado exitosamente")
            .credentials(collector.getCredentials())
            .counts(collector.toCounts())
            .credentialsFilePath(credentialsPath)
            .build();
    }

    private void seedReserve(SeedDataCollector collector) {
        if (reserveRepository.findFirstByOrderByCreatedAtDesc().isPresent()) {
            log.info("Fase Reserve: ya existe, omitiendo creación");
            return;
        }

        Reserve reserve = Reserve.builder()
            .initialBalance(properties.getInitialBalance())
            .reserveBalance(properties.getReserveBalance())
            .circulationBalance(properties.getCirculationBalance())
            .build();

        reserveRepository.save(reserve);
        collector.incrementReserves();
        log.info("Fase Reserve: reserva inicial creada");
    }

    private void seedSystemUsers(SeedDataCollector collector) {
        registerDevOrAdmin(
            collector,
            properties.getDevEmail(),
            properties.getDevPassword(),
            Role.ROLE_DEV.name()
        );
        registerDevOrAdmin(
            collector,
            properties.getAdminEmail(),
            properties.getAdminPassword(),
            Role.ROLE_ADMIN.name()
        );
        log.info("Fase Users: DEV y ADMIN verificados");
    }

    private void registerDevOrAdmin(SeedDataCollector collector, String email, String password, String role) {
        if (userExistService.validate(email)) {
            log.info("Usuario {} ya existe, omitiendo", email);
            return;
        }

        signUpService.signUp(
            SignUpRequestDto.builder().email(email).password(password).build(),
            role
        );

        collector.addCredential(SeedCredentialsDto.builder()
            .email(email)
            .password(password)
            .role(role)
            .name(role.replace("ROLE_", ""))
            .build());
    }

    private void seedAcademicStructure(SeedDataCollector collector) {
        Faker faker = new Faker(Locale.of("es", "AR"));
        Random random = new Random(42);
        Set<String> usedDnis = new HashSet<>();
        Set<String> usedEmails = new HashSet<>();

        List<String> yearNames = properties.getYearNames().stream()
            .limit(properties.getMaxYears())
            .toList();

        List<Course> allCourses = new ArrayList<>();

        for (String yearName : yearNames) {
            Year year = findOrCreateYear(yearName, collector);
            allCourses.addAll(findOrCreateCourses(year, collector));
        }

        List<Teacher> teachers = seedTeachers(faker, random, usedDnis, usedEmails, collector);

        int studentsPerCourse = properties.getMaxStudentsPerYear() / properties.getMaxCoursesPerYear();

        for (Course course : allCourses) {
            Year year = course.getYear();
            for (int i = 0; i < studentsPerCourse; i++) {
                registerStudent(faker, random, usedDnis, usedEmails, course, year, collector);
            }
        }

        seedSubjects(collector, teachers);
        log.info("Fase Admin: estructura académica creada");
    }

    private Year findOrCreateYear(String yearName, SeedDataCollector collector) {
        return yearRepository.findByName(yearName)
            .orElseGet(() -> {
                yearRegisterService.cu7RegisterYear(YearRequestDto.builder().name(yearName).build());
                collector.incrementYears();
                return yearRepository.findByName(yearName)
                    .orElseThrow(() -> new IllegalStateException("Año no encontrado tras creación: " + yearName));
            });
    }

    private List<Course> findOrCreateCourses(Year year, SeedDataCollector collector) {
        List<Course> courses = new ArrayList<>();

        for (String courseName : properties.getCourseNames().stream()
            .limit(properties.getMaxCoursesPerYear())
            .toList()) {

            Course course = StreamSupport.stream(courseRepository.findAllByDeletedAtIsNull().spliterator(), false)
                .filter(c -> c.getYear().getId().equals(year.getId()))
                .filter(c -> courseName.equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    courseRegisterService.cu6RegisterCourse(CourseRequestDto.builder()
                        .name(courseName)
                        .year_id(year.getId())
                        .build());
                    collector.incrementCourses();
                    return StreamSupport.stream(courseRepository.findAllByDeletedAtIsNull().spliterator(), false)
                        .filter(c -> c.getYear().getId().equals(year.getId()))
                        .filter(c -> courseName.equalsIgnoreCase(c.getName()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException(
                            "Curso no encontrado tras creación: " + courseName));
                });

            courses.add(course);
        }

        return courses;
    }

    private List<Teacher> seedTeachers(
        Faker faker,
        Random random,
        Set<String> usedDnis,
        Set<String> usedEmails,
        SeedDataCollector collector
    ) {
        List<Teacher> existing = new ArrayList<>();
        teacherRepository.findAll().forEach(existing::add);

        if (existing.size() >= properties.getMinTeachers()) {
            return existing;
        }

        int toCreate = properties.getMinTeachers() - existing.size();
        for (int i = 0; i < toCreate; i++) {
            String name = faker.name().firstName();
            String lastname = faker.name().lastName();
            String email = generateUniqueEmail(usedEmails, faker, name, lastname);
            String dni = generateUniqueDni(usedDnis, faker);

            teacherRegisterService.cu5RegisterTeacher(
                TeacherRequestDto.builder()
                    .name(name)
                    .lastname(lastname)
                    .email(email)
                    .dni(dni)
                    .build()
            );

            collector.incrementTeachers();
            collector.addCredential(SeedCredentialsDto.builder()
                .email(email)
                .password(dni)
                .role(Role.ROLE_TEACHER.name())
                .dni(dni)
                .name(name + " " + lastname)
                .build());
        }

        List<Teacher> all = new ArrayList<>();
        teacherRepository.findAll().forEach(all::add);
        return all;
    }

    private void registerStudent(
        Faker faker,
        Random random,
        Set<String> usedDnis,
        Set<String> usedEmails,
        Course course,
        Year year,
        SeedDataCollector collector
    ) {
        String name = faker.name().firstName();
        String lastname = faker.name().lastName();
        String email = generateUniqueEmail(usedEmails, faker, name, lastname);
        String dni = generateUniqueDni(usedDnis, faker);
        LocalDate birthdate = faker.date()
            .birthday(13, 18)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

        studentRegisterService.cu4registerStudent(
            StudentRequestDto.builder()
                .name(name)
                .lastname(lastname)
                .email(email)
                .dni(dni)
                .course_id(course.getId())
                .emailTutor(faker.internet().emailAddress())
                .birthDate(birthdate)
                .build()
        );

        collector.incrementStudents();
        collector.addCredential(SeedCredentialsDto.builder()
            .email(email)
            .password(dni)
            .role(Role.ROLE_STUDENT.name())
            .dni(dni)
            .name(name + " " + lastname)
            .yearName(year.getName())
            .courseName(course.getName())
            .build());
    }

    private void seedSubjects(SeedDataCollector collector, List<Teacher> teachers) {
        if (teachers.isEmpty()) {
            throw new IllegalStateException("Se requiere al menos un docente para crear materias");
        }

        Random random = new Random(42);
        List<String> subjectNames = properties.getDefaultSubjects().stream()
            .limit(properties.getMaxSubjectsPerCourse())
            .toList();

        Iterable<Course> courses = courseRepository.findAllByDeletedAtIsNull();
        for (Course course : courses) {
            for (String subjectName : subjectNames) {
                Teacher teacher = teachers.get(random.nextInt(teachers.size()));
                subjectRegisterService.cu28RegisterSubject(
                    SubjectRequestDto.builder()
                        .name(subjectName)
                        .courseId(course.getId())
                        .teacherId(teacher.getId())
                        .optional(false)
                        .build()
                );
                collector.incrementSubjects();
            }
        }
    }

    private void seedEconomy(SeedDataCollector collector) {
        subjectRefillBalanceService.cu58RefillBalance();
        log.info("Fase Economía: refill de materias ejecutado");

        Reserve lastReserve = reserveFindLastService.get();
        double walletAmount = properties.getWalletSeedAmount();

        Iterable<Student> students = studentRepository.findAll();
        for (Student student : students) {
            walletAddAmountService.execute(student.getWallet(), walletAmount);
            lastReserve.setCirculationBalance(lastReserve.getCirculationBalance() + walletAmount);
        }

        reserveRepository.save(lastReserve);
        log.info("Fase Economía: wallets fondeados con {}", walletAmount);
    }

    private void seedAspectsAndInventory(SeedDataCollector collector) {
        List<Aspect> aspects;
        try {
            aspects = aspectSeedService.seedCatalog(collector);
        } catch (IOException e) {
            throw new IllegalStateException("Error al registrar aspectos", e);
        }

        for (Student student : studentRepository.findAll()) {
            Profile profile = profileRepository.findById(student.getProfile().getId())
                .orElseThrow(() -> new IllegalStateException(
                    "Perfil no encontrado para estudiante " + student.getId()));

            for (Aspect aspect : aspects) {
                if (!profile.getOwnedAspects().contains(aspect)) {
                    profile.getOwnedAspects().add(aspect);
                }
            }
            profileRepository.save(profile);
        }

        log.info("Fase Aspectos: inventario asignado a estudiantes");
    }

    private String generateUniqueDni(Set<String> usedDnis, Faker faker) {
        String dni;
        do {
            dni = String.valueOf(faker.number().numberBetween(20_000_000, 40_000_000));
        } while (usedDnis.contains(dni));
        usedDnis.add(dni);
        return dni;
    }

    private String generateUniqueEmail(Set<String> usedEmails, Faker faker, String name, String lastname) {
        String email;
        do {
            email = name.toLowerCase(Locale.ROOT)
                + lastname.toLowerCase(Locale.ROOT)
                + faker.number().numberBetween(1, 1000)
                + "@gmail.com";
        } while (usedEmails.contains(email));
        usedEmails.add(email);
        return email;
    }
}
