package trinity.play2learn.backend.activity.preguntados.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
import trinity.play2learn.backend.activity.preguntados.PreguntadosTestMother;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.activity.preguntados.models.Preguntados;
import trinity.play2learn.backend.activity.preguntados.repositories.IPreguntadosRepository;
import trinity.play2learn.backend.activity.preguntados.services.interfaces.IPreguntadosValidateCorrectOptionService;
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
class PreguntadosGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IPreguntadosRepository preguntadosRepository;

    @Mock
    private ISubjectGetByIdService getSubjectByIdService;

    @Mock
    private IPreguntadosValidateCorrectOptionService preguntadosValidateCorrectOptionService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private PreguntadosGenerateService preguntadosGenerateService;

    @BeforeEach
    void setUp() {
        preguntadosGenerateService = new PreguntadosGenerateService(
            preguntadosRepository,
            getSubjectByIdService,
            preguntadosValidateCorrectOptionService,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu40GeneratePreguntados")
    class GeneratePreguntados {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating preguntados Then returns PreguntadosResponseDto")
        void shouldGeneratePreguntadosSuccessfully() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();
            Preguntados savedPreguntados = PreguntadosTestMother.savedPreguntados(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(preguntadosValidateCorrectOptionService).validateOneCorrectOption(any());
            when(preguntadosRepository.save(any(Preguntados.class))).thenReturn(savedPreguntados);
            when(transactionGenerateService.generate(
                any(TypeTransaction.class),
                any(Double.class),
                any(String.class),
                any(TransactionActor.class),
                any(TransactionActor.class),
                any(),
                any(Subject.class),
                any(Preguntados.class),
                any(),
                any(),
                any(),
                any()
            )).thenReturn(mock(trinity.play2learn.backend.economy.transaction.models.Transaction.class));

            // When
            PreguntadosResponseDto response = preguntadosGenerateService.cu40GeneratePreguntados(requestDto, user);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(ActivityTestMother.ACTIVITY_ID);
            assertThat(response.getName()).isEqualTo("Preguntados");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(preguntadosValidateCorrectOptionService, org.mockito.Mockito.times(5)).validateOneCorrectOption(any());
            verify(preguntadosRepository).save(any(Preguntados.class));
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(PreguntadosTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad de preguntados"),
                eq(TransactionActor.SISTEMA),
                eq(TransactionActor.SISTEMA),
                eq(null),
                eq(subject),
                eq(savedPreguntados),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
            );
        }

        @Test
        @DisplayName("Given subject not found When generating preguntados Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("La materia no fue encontrada"));

            // When & Then
            assertThatThrownBy(() -> preguntadosGenerateService.cu40GeneratePreguntados(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("La materia no fue encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(preguntadosValidateCorrectOptionService, never()).validateOneCorrectOption(any());
            verify(preguntadosRepository, never()).save(any());
            verify(transactionGenerateService, never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating preguntados Then throws ConflictException")
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
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> preguntadosGenerateService.cu40GeneratePreguntados(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(preguntadosValidateCorrectOptionService, never()).validateOneCorrectOption(any());
            verify(preguntadosRepository, never()).save(any());
            verify(transactionGenerateService, never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given question without correct option (pregunta sin opción correcta - edge case) When generating preguntados Then throws BadRequestException")
        void shouldThrowBadRequestExceptionWhenQuestionWithoutCorrectOption() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doThrow(new BadRequestException("Debe haber exactamente una opción correcta por pregunta"))
                .when(preguntadosValidateCorrectOptionService).validateOneCorrectOption(any());

            // When & Then
            assertThatThrownBy(() -> preguntadosGenerateService.cu40GeneratePreguntados(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Debe haber exactamente una opción correcta por pregunta");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(preguntadosValidateCorrectOptionService).validateOneCorrectOption(any());
            verify(preguntadosRepository, never()).save(any());
            verify(transactionGenerateService, never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given valid request When generating preguntados Then verifies saved fields with ArgumentCaptor")
        void shouldVerifySavedFields() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();
            Preguntados savedPreguntados = PreguntadosTestMother.savedPreguntados(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(preguntadosValidateCorrectOptionService).validateOneCorrectOption(any());
            when(preguntadosRepository.save(any(Preguntados.class))).thenReturn(savedPreguntados);
            when(transactionGenerateService.generate(
                any(TypeTransaction.class),
                any(Double.class),
                any(String.class),
                any(TransactionActor.class),
                any(TransactionActor.class),
                any(),
                any(Subject.class),
                any(Preguntados.class),
                any(),
                any(),
                any(),
                any()
            )).thenReturn(mock(trinity.play2learn.backend.economy.transaction.models.Transaction.class));

            // When
            preguntadosGenerateService.cu40GeneratePreguntados(requestDto, user);

            // Then
            ArgumentCaptor<Preguntados> preguntadosCaptor = ArgumentCaptor.forClass(Preguntados.class);
            verify(preguntadosRepository).save(preguntadosCaptor.capture());

            Preguntados capturedPreguntados = preguntadosCaptor.getValue();
            assertThat(capturedPreguntados)
                .extracting(
                    Preguntados::getName,
                    Preguntados::getDescription,
                    Preguntados::getSubject,
                    Preguntados::getInitialBalance,
                    Preguntados::getMaxTimePerQuestion
                )
                .containsExactly(
                    "Preguntados",
                    requestDto.getDescription(),
                    subject,
                    PreguntadosTestMother.DEFAULT_INITIAL_BALANCE,
                    requestDto.getMaxTimePerQuestionInSeconds()
                );
        }
    }
}

