package trinity.play2learn.backend.activity.clasificacion.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.clasificacion.ClasificacionTestMother;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.models.ClasificacionActivity;
import trinity.play2learn.backend.activity.clasificacion.repositories.IClasificacionActivityRepository;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateCategoriesNamesService;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateConceptsNamesService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ClasificacionActivityGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IClasificacionActivityRepository clasificacionRepository;

    @Mock
    private ISubjectGetByIdService subjectGetService;

    @Mock
    private IClasificacionValidateCategoriesNamesService validateCategoriesNamesService;

    @Mock
    private IClasificacionValidateConceptsNamesService validateConceptsNamesService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private ClasificacionActivityGenerateService clasificacionActivityGenerateService;

    @BeforeEach
    void setUp() {
        clasificacionActivityGenerateService = new ClasificacionActivityGenerateService(
            clasificacionRepository,
            subjectGetService,
            validateCategoriesNamesService,
            validateConceptsNamesService,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu43GenerateClasificacionActivity")
    class GenerateClasificacionActivity {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating clasificacion Then returns ClasificacionActivityResponseDto")
        void shouldGenerateClasificacionSuccessfully() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();
            ClasificacionActivity savedActivity = ClasificacionTestMother.savedClasificacion(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(validateCategoriesNamesService).validateCategoriesNames(any());
            doNothing().when(validateConceptsNamesService).validateDuplicateConceptsNames(any());
            when(clasificacionRepository.save(any(ClasificacionActivity.class))).thenReturn(savedActivity);

            // When
            ClasificacionActivityResponseDto result = clasificacionActivityGenerateService.cu43GenerateClasificacionActivity(requestDto, user);

            // Then
            assertThat(result)
                .isNotNull()
                .extracting(
                    ClasificacionActivityResponseDto::getId,
                    ClasificacionActivityResponseDto::getName
                )
                .containsExactly(
                    savedActivity.getId(),
                    "Desafio de clasificacion"
                );

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateCategoriesNamesService).validateCategoriesNames(requestDto);
            verify(validateConceptsNamesService).validateDuplicateConceptsNames(requestDto);
            verify(clasificacionRepository).save(any(ClasificacionActivity.class));
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(ClasificacionTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad de clasificación"),
                eq(TransactionActor.SISTEMA),
                eq(TransactionActor.SISTEMA),
                eq(null),
                eq(subject),
                eq(savedActivity),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
            );
        }

        @Test
        @DisplayName("Given subject not found When generating clasificacion Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("Materia no encontrada"));

            // When & Then
            assertThatThrownBy(() -> clasificacionActivityGenerateService.cu43GenerateClasificacionActivity(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Materia no encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateCategoriesNamesService, never()).validateCategoriesNames(any());
            verify(validateConceptsNamesService, never()).validateDuplicateConceptsNames(any());
            verify(clasificacionRepository, never()).save(any(ClasificacionActivity.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating clasificacion Then throws ConflictException")
        void shouldThrowConflictExceptionWhenTeacherNotAssigned() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Teacher otherTeacher = ActivityTestMother.teacher(500L, "other@example.com");
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                otherTeacher
            );
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> clasificacionActivityGenerateService.cu43GenerateClasificacionActivity(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateCategoriesNamesService, never()).validateCategoriesNames(any());
            verify(validateConceptsNamesService, never()).validateDuplicateConceptsNames(any());
            verify(clasificacionRepository, never()).save(any(ClasificacionActivity.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given duplicate category names When generating clasificacion Then throws BadRequestException")
        void shouldThrowBadRequestExceptionWhenDuplicateCategoryNames() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doThrow(new BadRequestException("The categories names must be unique."))
                .when(validateCategoriesNamesService).validateCategoriesNames(requestDto);

            // When & Then
            assertThatThrownBy(() -> clasificacionActivityGenerateService.cu43GenerateClasificacionActivity(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("The categories names must be unique.");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateCategoriesNamesService).validateCategoriesNames(requestDto);
            verify(validateConceptsNamesService, never()).validateDuplicateConceptsNames(any());
            verify(clasificacionRepository, never()).save(any(ClasificacionActivity.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given duplicate concept names When generating clasificacion Then throws BadRequestException")
        void shouldThrowBadRequestExceptionWhenDuplicateConceptNames() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(validateCategoriesNamesService).validateCategoriesNames(any());
            doThrow(new BadRequestException("Los nombres de los conceptos deben ser unicos."))
                .when(validateConceptsNamesService).validateDuplicateConceptsNames(requestDto);

            // When & Then
            assertThatThrownBy(() -> clasificacionActivityGenerateService.cu43GenerateClasificacionActivity(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Los nombres de los conceptos deben ser unicos.");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateCategoriesNamesService).validateCategoriesNames(requestDto);
            verify(validateConceptsNamesService).validateDuplicateConceptsNames(requestDto);
            verify(clasificacionRepository, never()).save(any(ClasificacionActivity.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given valid request When generating clasificacion Then saves activity with correct fields")
        void shouldSaveClasificacionWithCorrectFields() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();
            ClasificacionActivity savedActivity = ClasificacionTestMother.savedClasificacion(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(validateCategoriesNamesService).validateCategoriesNames(any());
            doNothing().when(validateConceptsNamesService).validateDuplicateConceptsNames(any());
            when(clasificacionRepository.save(any(ClasificacionActivity.class))).thenReturn(savedActivity);

            // When
            clasificacionActivityGenerateService.cu43GenerateClasificacionActivity(requestDto, user);

            // Then
            ArgumentCaptor<ClasificacionActivity> activityCaptor = ArgumentCaptor.forClass(ClasificacionActivity.class);
            verify(clasificacionRepository).save(activityCaptor.capture());

            ClasificacionActivity capturedActivity = activityCaptor.getValue();
            assertThat(capturedActivity)
                .extracting(
                    ClasificacionActivity::getName,
                    ClasificacionActivity::getDescription,
                    ClasificacionActivity::getSubject,
                    ClasificacionActivity::getInitialBalance
                )
                .containsExactly(
                    "Desafio de clasificacion",
                    requestDto.getDescription(),
                    subject,
                    ClasificacionTestMother.DEFAULT_INITIAL_BALANCE
                );
        }
    }
}

