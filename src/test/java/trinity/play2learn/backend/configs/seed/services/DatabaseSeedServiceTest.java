package trinity.play2learn.backend.configs.seed.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseRegisterService;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentRegisterService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRefillBalanceService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRegisterService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRegisterService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearRegisterService;
import trinity.play2learn.backend.configs.seed.config.DatabaseSeedProperties;
import trinity.play2learn.backend.configs.seed.dtos.SeedResultDto;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.user.dtos.signUp.SignUpResponseDto;
import trinity.play2learn.backend.user.services.signUp.interfaces.ISignUpService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserExistService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DatabaseSeedServiceTest {

    @Mock
    private DatabaseSeedProperties properties;

    @Mock
    private IReserveRepository reserveRepository;

    @Mock
    private IReserveFindLastService reserveFindLastService;

    @Mock
    private ISignUpService signUpService;

    @Mock
    private IUserExistService userExistService;

    @Mock
    private IYearRegisterService yearRegisterService;

    @Mock
    private IYearRepository yearRepository;

    @Mock
    private ICourseRegisterService courseRegisterService;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private ITeacherRegisterService teacherRegisterService;

    @Mock
    private ITeacherRepository teacherRepository;

    @Mock
    private IStudentRegisterService studentRegisterService;

    @Mock
    private IStudentRepository studentRepository;

    @Mock
    private ISubjectRegisterService subjectRegisterService;

    @Mock
    private ISubjectRefillBalanceService subjectRefillBalanceService;

    @Mock
    private IWalletAddAmountService walletAddAmountService;

    @Mock
    private AspectSeedService aspectSeedService;

    @Mock
    private IProfileRepository profileRepository;

    @Mock
    private CredentialsMarkdownWriter credentialsMarkdownWriter;

    @InjectMocks
    private DatabaseSeedService databaseSeedService;

    @BeforeEach
    void setUpProperties() throws IOException {
        when(properties.getDevEmail()).thenReturn("dev@gmail.com");
        when(properties.getDevPassword()).thenReturn("12345678");
        when(properties.getAdminEmail()).thenReturn("admin@gmail.com");
        when(properties.getAdminPassword()).thenReturn("12345678");
        when(properties.getInitialBalance()).thenReturn(5_000_000.0);
        when(properties.getReserveBalance()).thenReturn(5_000_000.0);
        when(properties.getCirculationBalance()).thenReturn(0.0);
        when(properties.getMaxYears()).thenReturn(6);
        when(properties.getMaxCoursesPerYear()).thenReturn(2);
        when(properties.getMaxSubjectsPerCourse()).thenReturn(3);
        when(properties.getMaxStudentsPerYear()).thenReturn(30);
        when(properties.getMinTeachers()).thenReturn(6);
        when(properties.getYearNames()).thenReturn(List.of("Primer Año"));
        when(properties.getCourseNames()).thenReturn(List.of("A", "B"));
        when(properties.getDefaultSubjects()).thenReturn(List.of("Matemática", "Lengua", "Geografía"));
        when(properties.getWalletSeedAmount()).thenReturn(3000.0);
        when(credentialsMarkdownWriter.write(any(), any())).thenReturn("docs/seed/credentials.md");
        when(aspectSeedService.seedCatalog(any())).thenReturn(List.of());
    }

    private void stubEmptyAcademicStructure() {
        Year year = Year.builder().id(1L).name("Primer Año").build();
        when(yearRepository.findByName("Primer Año"))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(year));

        List<Course> courses = new ArrayList<>();
        when(courseRepository.findAllByDeletedAtIsNull()).thenAnswer(invocation -> List.copyOf(courses));
        when(courseRegisterService.cu6RegisterCourse(any())).thenAnswer(invocation -> {
            var dto = invocation.getArgument(0, trinity.play2learn.backend.admin.course.dtos.CourseRequestDto.class);
            Course course = Course.builder()
                .id((long) courses.size() + 1)
                .name(dto.getName())
                .year(year)
                .build();
            courses.add(course);
            return null;
        });

        List<Teacher> teachers = new ArrayList<>();
        when(teacherRepository.findAll()).thenAnswer(invocation -> List.copyOf(teachers));
        when(teacherRegisterService.cu5RegisterTeacher(any())).thenAnswer(invocation -> {
            var dto = invocation.getArgument(0, trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto.class);
            Teacher teacher = Teacher.builder()
                .id((long) teachers.size() + 1)
                .name(dto.getName())
                .lastname(dto.getLastname())
                .dni(dto.getDni())
                .build();
            teachers.add(teacher);
            return trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto.builder()
                .id(teacher.getId())
                .name(dto.getName())
                .build();
        });

        when(studentRegisterService.cu4registerStudent(any())).thenReturn(null);

        when(studentRepository.findAll()).thenReturn(List.of());
    }

    @Test
    @DisplayName("Given empty reserve When execute Then creates reserve")
    void whenEmptyReserve_createsReserve() throws IOException {
        when(reserveRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
        when(userExistService.validate(any())).thenReturn(true);
        stubEmptyAcademicStructure();

        SeedResultDto result = databaseSeedService.execute();

        verify(reserveRepository).save(any(Reserve.class));
        assertThat(result.getMessage()).contains("completado");
    }

    @Test
    @DisplayName("Given existing reserve When execute Then skips reserve creation")
    void whenReserveExists_skipsCreation() throws IOException {
        when(reserveRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(new Reserve()));
        when(userExistService.validate(any())).thenReturn(true);
        stubEmptyAcademicStructure();
        when(reserveFindLastService.get()).thenReturn(new Reserve());

        databaseSeedService.execute();

        verify(reserveRepository, never()).save(
            org.mockito.ArgumentMatchers.argThat((Reserve r) -> r.getInitialBalance() != null)
        );
    }

    @Test
    @DisplayName("Given no users When execute Then registers DEV and ADMIN")
    void whenNoUsers_registersDevAndAdmin() throws IOException {
        when(reserveRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(new Reserve()));
        when(userExistService.validate(any())).thenReturn(false);
        when(signUpService.signUp(any(), eq("ROLE_DEV"))).thenReturn(SignUpResponseDto.builder().build());
        when(signUpService.signUp(any(), eq("ROLE_ADMIN"))).thenReturn(SignUpResponseDto.builder().build());
        stubEmptyAcademicStructure();
        when(reserveFindLastService.get()).thenReturn(new Reserve());

        SeedResultDto result = databaseSeedService.execute();

        verify(signUpService).signUp(any(), eq("ROLE_DEV"));
        verify(signUpService).signUp(any(), eq("ROLE_ADMIN"));
        assertThat(result.getCredentials().stream()
            .filter(c -> "ROLE_DEV".equals(c.getRole()) || "ROLE_ADMIN".equals(c.getRole()))
            .toList()).hasSize(2);
    }

    @Test
    @DisplayName("Given empty data When execute Then refills subjects and seeds aspects")
    void whenExecute_runsEconomyAndAspectPhases() throws IOException {
        when(reserveRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(new Reserve()));
        when(userExistService.validate(any())).thenReturn(true);
        stubEmptyAcademicStructure();
        when(reserveFindLastService.get()).thenReturn(Reserve.builder().circulationBalance(0.0).build());

        databaseSeedService.execute();

        verify(subjectRefillBalanceService).cu58RefillBalance();
        verify(aspectSeedService).seedCatalog(any());
    }
}
