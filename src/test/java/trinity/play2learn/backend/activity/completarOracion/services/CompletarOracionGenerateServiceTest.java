package trinity.play2learn.backend.activity.completarOracion.services;

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
import trinity.play2learn.backend.activity.completarOracion.CompletarOracionTestMother;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.activity.completarOracion.models.CompletarOracionActivity;
import trinity.play2learn.backend.activity.completarOracion.repositories.ICompletarOracionRepository;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionValidateWordMissingService;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionValidateWordsOrderService;
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
class CompletarOracionGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private ICompletarOracionRepository completarOracionRepository;

    @Mock
    private ISubjectGetByIdService getSubjectByIdService;

    @Mock
    private ICompletarOracionValidateWordsOrderService completarOracionValidateWordsOrderService;

    @Mock
    private ICompletarOracionValidateWordMissingService completarOracionValidateWordMissingService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private CompletarOracionGenerateService completarOracionGenerateService;

    @BeforeEach
    void setUp() {
        completarOracionGenerateService = new CompletarOracionGenerateService(
            completarOracionRepository,
            getSubjectByIdService,
            completarOracionValidateWordsOrderService,
            completarOracionValidateWordMissingService,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu42generateCompletarOracionActivity")
    class GenerateCompletarOracionActivity {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating completar oracion Then returns CompletarOracionActivityResponseDto")
        void shouldGenerateCompletarOracionSuccessfully() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();
            CompletarOracionActivity savedActivity = CompletarOracionTestMother.savedCompletarOracion(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(completarOracionValidateWordsOrderService).validateWordsOrder(any());
            doNothing().when(completarOracionValidateWordMissingService).validateAtLeastOneWordMissing(any());
            when(completarOracionRepository.save(any(CompletarOracionActivity.class))).thenReturn(savedActivity);

            // When
            CompletarOracionActivityResponseDto result = completarOracionGenerateService.cu42generateCompletarOracionActivity(requestDto, user);

            // Then
            assertThat(result)
                .isNotNull()
                .extracting(
                    CompletarOracionActivityResponseDto::getId,
                    CompletarOracionActivityResponseDto::getName
                )
                .containsExactly(
                    savedActivity.getId(),
                    "Completar oracion"
                );

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(completarOracionValidateWordsOrderService).validateWordsOrder(any());
            verify(completarOracionValidateWordMissingService).validateAtLeastOneWordMissing(any());
            verify(completarOracionRepository).save(any(CompletarOracionActivity.class));
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(CompletarOracionTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad de ahorcado"),
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
        @DisplayName("Given subject not found When generating completar oracion Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("Materia no encontrada"));

            // When & Then
            assertThatThrownBy(() -> completarOracionGenerateService.cu42generateCompletarOracionActivity(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Materia no encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(completarOracionValidateWordsOrderService, never()).validateWordsOrder(any());
            verify(completarOracionValidateWordMissingService, never()).validateAtLeastOneWordMissing(any());
            verify(completarOracionRepository, never()).save(any(CompletarOracionActivity.class));
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating completar oracion Then throws ConflictException")
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
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> completarOracionGenerateService.cu42generateCompletarOracionActivity(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(completarOracionValidateWordsOrderService, never()).validateWordsOrder(any());
            verify(completarOracionValidateWordMissingService, never()).validateAtLeastOneWordMissing(any());
            verify(completarOracionRepository, never()).save(any(CompletarOracionActivity.class));
        }

        @Test
        @DisplayName("Given invalid words order When generating completar oracion Then throws BadRequestException")
        void shouldThrowBadRequestExceptionWhenInvalidWordsOrder() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doThrow(new BadRequestException("Los ordenes de las palabras no pueden repetirse."))
                .when(completarOracionValidateWordsOrderService).validateWordsOrder(any());

            // When & Then
            assertThatThrownBy(() -> completarOracionGenerateService.cu42generateCompletarOracionActivity(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Los ordenes de las palabras no pueden repetirse.");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(completarOracionValidateWordsOrderService).validateWordsOrder(any());
            verify(completarOracionValidateWordMissingService, never()).validateAtLeastOneWordMissing(any());
            verify(completarOracionRepository, never()).save(any(CompletarOracionActivity.class));
        }

        @Test
        @DisplayName("Given no words missing When generating completar oracion Then throws BadRequestException")
        void shouldThrowBadRequestExceptionWhenNoWordsMissing() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(completarOracionValidateWordsOrderService).validateWordsOrder(any());
            doThrow(new BadRequestException("Cada oracion debe tener al menos una palabra faltante."))
                .when(completarOracionValidateWordMissingService).validateAtLeastOneWordMissing(any());

            // When & Then
            assertThatThrownBy(() -> completarOracionGenerateService.cu42generateCompletarOracionActivity(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cada oracion debe tener al menos una palabra faltante.");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(completarOracionValidateWordsOrderService).validateWordsOrder(any());
            verify(completarOracionValidateWordMissingService).validateAtLeastOneWordMissing(any());
            verify(completarOracionRepository, never()).save(any(CompletarOracionActivity.class));
        }

        @Test
        @DisplayName("Given valid request When generating completar oracion Then saves activity with correct fields")
        void shouldSaveActivityWithCorrectFields() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();
            CompletarOracionActivity savedActivity = CompletarOracionTestMother.savedCompletarOracion(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(completarOracionValidateWordsOrderService).validateWordsOrder(any());
            doNothing().when(completarOracionValidateWordMissingService).validateAtLeastOneWordMissing(any());
            when(completarOracionRepository.save(any(CompletarOracionActivity.class))).thenReturn(savedActivity);

            // When
            completarOracionGenerateService.cu42generateCompletarOracionActivity(requestDto, user);

            // Then
            ArgumentCaptor<CompletarOracionActivity> activityCaptor = ArgumentCaptor.forClass(CompletarOracionActivity.class);
            verify(completarOracionRepository).save(activityCaptor.capture());

            CompletarOracionActivity capturedActivity = activityCaptor.getValue();
            assertThat(capturedActivity)
                .extracting(
                    CompletarOracionActivity::getName,
                    CompletarOracionActivity::getDescription,
                    CompletarOracionActivity::getSubject,
                    CompletarOracionActivity::getInitialBalance
                )
                .containsExactly(
                    "Completar oracion",
                    requestDto.getDescription(),
                    subject,
                    CompletarOracionTestMother.DEFAULT_INITIAL_BALANCE
                );
        }
    }
}

