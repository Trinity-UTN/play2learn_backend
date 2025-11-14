package trinity.play2learn.backend.activity.ahorcado.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import trinity.play2learn.backend.activity.ahorcado.AhorcadoTestMother;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;
import trinity.play2learn.backend.activity.ahorcado.repositories.IAhorcadoRepository;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class AhorcadoGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IAhorcadoRepository ahorcadoRepository;

    @Mock
    private ISubjectGetByIdService getSubjectByIdService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private AhorcadoGenerateService ahorcadoGenerateService;

    @BeforeEach
    void setUp() {
        ahorcadoGenerateService = new AhorcadoGenerateService(
            ahorcadoRepository,
            getSubjectByIdService,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu39GenerateAhorcado")
    class GenerateAhorcado {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating ahorcado Then returns AhorcadoResponseDto")
        void shouldGenerateAhorcadoSuccessfully() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            AhorcadoRequestDto requestDto = AhorcadoTestMother.validAhorcadoRequestDto();
            Ahorcado savedAhorcado = AhorcadoTestMother.savedAhorcado(ActivityTestMother.ACTIVITY_ID, subject);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(ahorcadoRepository.save(any(Ahorcado.class))).thenReturn(savedAhorcado);

            // When
            AhorcadoResponseDto result = ahorcadoGenerateService.cu39GenerateAhorcado(requestDto, user);

            // Then
            assertThat(result)
                .isNotNull()
                .extracting(
                    AhorcadoResponseDto::getId,
                    AhorcadoResponseDto::getName,
                    AhorcadoResponseDto::getWord,
                    AhorcadoResponseDto::getErrorsPermited
                )
                .containsExactly(
                    savedAhorcado.getId(),
                    "Ahorcado",
                    requestDto.getWord(),
                    savedAhorcado.getErrorsPermited().getValue()
                );

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(ahorcadoRepository).save(any(Ahorcado.class));
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(AhorcadoTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad de ahorcado"),
                eq(TransactionActor.SISTEMA),
                eq(TransactionActor.SISTEMA),
                eq(null),
                eq(subject),
                eq(savedAhorcado),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
            );
        }

        @Test
        @DisplayName("Given subject not found When generating ahorcado Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            AhorcadoRequestDto requestDto = AhorcadoTestMother.validAhorcadoRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("Materia no encontrada"));

            // When & Then
            assertThatThrownBy(() -> ahorcadoGenerateService.cu39GenerateAhorcado(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Materia no encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(ahorcadoRepository, never()).save(any(Ahorcado.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating ahorcado Then throws ConflictException")
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
            AhorcadoRequestDto requestDto = AhorcadoTestMother.validAhorcadoRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> ahorcadoGenerateService.cu39GenerateAhorcado(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(ahorcadoRepository, never()).save(any(Ahorcado.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given valid request When generating ahorcado Then saves ahorcado with correct fields")
        void shouldSaveAhorcadoWithCorrectFields() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            AhorcadoRequestDto requestDto = AhorcadoTestMother.validAhorcadoRequestDto();
            Ahorcado savedAhorcado = AhorcadoTestMother.savedAhorcado(ActivityTestMother.ACTIVITY_ID, subject);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(ahorcadoRepository.save(any(Ahorcado.class))).thenReturn(savedAhorcado);

            // When
            ahorcadoGenerateService.cu39GenerateAhorcado(requestDto, user);

            // Then
            ArgumentCaptor<Ahorcado> ahorcadoCaptor = ArgumentCaptor.forClass(Ahorcado.class);
            verify(ahorcadoRepository).save(ahorcadoCaptor.capture());

            Ahorcado capturedAhorcado = ahorcadoCaptor.getValue();
            assertThat(capturedAhorcado)
                .extracting(
                    Ahorcado::getName,
                    Ahorcado::getDescription,
                    Ahorcado::getWord,
                    Ahorcado::getErrorsPermited,
                    Ahorcado::getSubject,
                    Ahorcado::getInitialBalance
                )
                .containsExactly(
                    "Ahorcado",
                    requestDto.getDescription(),
                    requestDto.getWord(),
                    requestDto.getErrorsPermited(),
                    subject,
                    AhorcadoTestMother.DEFAULT_INITIAL_BALANCE
                );
        }
    }
}

