package trinity.play2learn.backend.activity.noLudica.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
import trinity.play2learn.backend.activity.noLudica.NoLudicaTestMother;
import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.activity.noLudica.models.NoLudica;
import trinity.play2learn.backend.activity.noLudica.repositories.INoLudicaRepository;
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
class NoLudicaGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private INoLudicaRepository noLudicaRepository;

    @Mock
    private ISubjectGetByIdService findSubjectByIdService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private NoLudicaGenerateService noLudicaGenerateService;

    @BeforeEach
    void setUp() {
        noLudicaGenerateService = new NoLudicaGenerateService(
            findSubjectByIdService,
            noLudicaRepository,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu45GenerateNoLudica")
    class GenerateNoLudica {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating no ludica Then returns NoLudicaResponseDto")
        void shouldGenerateNoLudicaSuccessfully() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            NoLudicaRequestDto requestDto = NoLudicaTestMother.validNoLudicaRequestDto();
            NoLudica savedNoLudica = NoLudicaTestMother.savedNoLudica(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(noLudicaRepository.save(any(NoLudica.class))).thenReturn(savedNoLudica);
            when(transactionGenerateService.generate(
                any(TypeTransaction.class),
                any(Double.class),
                any(String.class),
                any(TransactionActor.class),
                any(TransactionActor.class),
                any(),
                any(Subject.class),
                any(NoLudica.class),
                any(),
                any(),
                any(),
                any()
            )).thenReturn(mock(trinity.play2learn.backend.economy.transaction.models.Transaction.class));

            // When
            NoLudicaResponseDto response = noLudicaGenerateService.cu45GenerateNoLudica(requestDto, user);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(ActivityTestMother.ACTIVITY_ID);
            assertThat(response.getName()).isEqualTo("No Ludica");
            assertThat(response.getExcercise()).isEqualTo(requestDto.getExcercise());
            assertThat(response.getTipoEntrega()).isEqualTo(requestDto.getTipoEntrega().name());

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(findSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(noLudicaRepository).save(any(NoLudica.class));
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(NoLudicaTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad No Ludica"),
                eq(TransactionActor.SISTEMA),
                eq(TransactionActor.SISTEMA),
                eq(null),
                eq(subject),
                eq(savedNoLudica),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
            );
        }

        @Test
        @DisplayName("Given subject not found When generating no ludica Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            NoLudicaRequestDto requestDto = NoLudicaTestMother.validNoLudicaRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("La materia no fue encontrada"));

            // When & Then
            assertThatThrownBy(() -> noLudicaGenerateService.cu45GenerateNoLudica(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("La materia no fue encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(findSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(noLudicaRepository, org.mockito.Mockito.never()).save(any());
            verify(transactionGenerateService, org.mockito.Mockito.never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating no ludica Then throws ConflictException")
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
            NoLudicaRequestDto requestDto = NoLudicaTestMother.validNoLudicaRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> noLudicaGenerateService.cu45GenerateNoLudica(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(findSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(noLudicaRepository, org.mockito.Mockito.never()).save(any());
            verify(transactionGenerateService, org.mockito.Mockito.never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given valid request When generating no ludica Then verifies saved fields with ArgumentCaptor")
        void shouldVerifySavedFields() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            NoLudicaRequestDto requestDto = NoLudicaTestMother.validNoLudicaRequestDto();
            NoLudica savedNoLudica = NoLudicaTestMother.savedNoLudica(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(noLudicaRepository.save(any(NoLudica.class))).thenReturn(savedNoLudica);
            when(transactionGenerateService.generate(
                any(TypeTransaction.class),
                any(Double.class),
                any(String.class),
                any(TransactionActor.class),
                any(TransactionActor.class),
                any(),
                any(Subject.class),
                any(NoLudica.class),
                any(),
                any(),
                any(),
                any()
            )).thenReturn(mock(trinity.play2learn.backend.economy.transaction.models.Transaction.class));

            // When
            noLudicaGenerateService.cu45GenerateNoLudica(requestDto, user);

            // Then
            ArgumentCaptor<NoLudica> noLudicaCaptor = ArgumentCaptor.forClass(NoLudica.class);
            verify(noLudicaRepository).save(noLudicaCaptor.capture());

            NoLudica capturedNoLudica = noLudicaCaptor.getValue();
            assertThat(capturedNoLudica)
                .extracting(
                    NoLudica::getName,
                    NoLudica::getDescription,
                    NoLudica::getSubject,
                    NoLudica::getInitialBalance,
                    NoLudica::getExcercise,
                    NoLudica::getTipoEntrega
                )
                .containsExactly(
                    "No Ludica",
                    requestDto.getDescription(),
                    subject,
                    NoLudicaTestMother.DEFAULT_INITIAL_BALANCE,
                    requestDto.getExcercise(),
                    requestDto.getTipoEntrega()
                );
        }
    }
}

