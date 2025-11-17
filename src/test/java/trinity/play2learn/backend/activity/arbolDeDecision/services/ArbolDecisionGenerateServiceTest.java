package trinity.play2learn.backend.activity.arbolDeDecision.services;

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
import trinity.play2learn.backend.activity.arbolDeDecision.ArbolDeDecisionTestMother;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.models.ArbolDeDecisionActivity;
import trinity.play2learn.backend.activity.arbolDeDecision.repositories.IArbolDeDecisionRepository;
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
class ArbolDecisionGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IArbolDeDecisionRepository arbolDeDecisionRepository;

    @Mock
    private ISubjectGetByIdService getSubjectByIdService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private ArbolDecisionGenerateService arbolDecisionGenerateService;

    @BeforeEach
    void setUp() {
        arbolDecisionGenerateService = new ArbolDecisionGenerateService(
            arbolDeDecisionRepository,
            getSubjectByIdService,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu46GenerateArbolDeDecisionActivity")
    class GenerateArbolDeDecisionActivity {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating arbol de decision Then returns ArbolDeDecisionActivityResponseDto")
        void shouldGenerateArbolDeDecisionSuccessfully() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            ArbolDeDecisionActivityRequestDto requestDto = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();
            ArbolDeDecisionActivity savedActivity = ArbolDeDecisionTestMother.savedArbolDeDecision(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(arbolDeDecisionRepository.save(any(ArbolDeDecisionActivity.class))).thenReturn(savedActivity);

            // When
            ArbolDeDecisionActivityResponseDto result = arbolDecisionGenerateService.cu46GenerateArbolDeDecisionActivity(requestDto, user);

            // Then
            assertThat(result)
                .isNotNull()
                .extracting(
                    ArbolDeDecisionActivityResponseDto::getId,
                    ArbolDeDecisionActivityResponseDto::getName,
                    ArbolDeDecisionActivityResponseDto::getIntroduction
                )
                .containsExactly(
                    savedActivity.getId(),
                    "Arbol de decision",
                    requestDto.getIntroduction()
                );

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(arbolDeDecisionRepository).save(any(ArbolDeDecisionActivity.class));
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(ArbolDeDecisionTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad de árbol de decisión"),
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
        @DisplayName("Given subject not found When generating arbol de decision Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            ArbolDeDecisionActivityRequestDto requestDto = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("Materia no encontrada"));

            // When & Then
            assertThatThrownBy(() -> arbolDecisionGenerateService.cu46GenerateArbolDeDecisionActivity(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Materia no encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(arbolDeDecisionRepository, never()).save(any(ArbolDeDecisionActivity.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating arbol de decision Then throws ConflictException")
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
            ArbolDeDecisionActivityRequestDto requestDto = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> arbolDecisionGenerateService.cu46GenerateArbolDeDecisionActivity(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(getSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(arbolDeDecisionRepository, never()).save(any(ArbolDeDecisionActivity.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given valid request When generating arbol de decision Then saves activity with correct fields")
        void shouldSaveArbolDeDecisionWithCorrectFields() {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            ArbolDeDecisionActivityRequestDto requestDto = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();
            ArbolDeDecisionActivity savedActivity = ArbolDeDecisionTestMother.savedArbolDeDecision(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(getSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(arbolDeDecisionRepository.save(any(ArbolDeDecisionActivity.class))).thenReturn(savedActivity);

            // When
            arbolDecisionGenerateService.cu46GenerateArbolDeDecisionActivity(requestDto, user);

            // Then
            ArgumentCaptor<ArbolDeDecisionActivity> activityCaptor = ArgumentCaptor.forClass(ArbolDeDecisionActivity.class);
            verify(arbolDeDecisionRepository).save(activityCaptor.capture());

            ArbolDeDecisionActivity capturedActivity = activityCaptor.getValue();
            assertThat(capturedActivity)
                .extracting(
                    ArbolDeDecisionActivity::getName,
                    ArbolDeDecisionActivity::getDescription,
                    ArbolDeDecisionActivity::getIntroduction,
                    ArbolDeDecisionActivity::getSubject,
                    ArbolDeDecisionActivity::getInitialBalance
                )
                .containsExactly(
                    "Arbol de decision",
                    requestDto.getDescription(),
                    requestDto.getIntroduction(),
                    subject,
                    ArbolDeDecisionTestMother.DEFAULT_INITIAL_BALANCE
                );
        }
    }
}

