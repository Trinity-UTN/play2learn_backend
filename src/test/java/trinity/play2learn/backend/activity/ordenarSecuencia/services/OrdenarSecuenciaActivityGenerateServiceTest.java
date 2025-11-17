package trinity.play2learn.backend.activity.ordenarSecuencia.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.ordenarSecuencia.OrdenarSecuenciaTestMother;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;
import trinity.play2learn.backend.activity.ordenarSecuencia.repositories.IOrdenarSecuenciaRepository;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IEventsGenerateService;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IValidateEvents;
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
class OrdenarSecuenciaActivityGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IOrdenarSecuenciaRepository ordenarSecuenciaRepository;

    @Mock
    private ISubjectGetByIdService subjectGetService;

    @Mock
    private IValidateEvents validateEvents;

    @Mock
    private IEventsGenerateService eventsGenerateService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private OrdenarSecuenciaActivityGenerateService ordenarSecuenciaGenerateService;

    @BeforeEach
    void setUp() {
        ordenarSecuenciaGenerateService = new OrdenarSecuenciaActivityGenerateService(
            subjectGetService,
            validateEvents,
            ordenarSecuenciaRepository,
            eventsGenerateService,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu44GenerateOrdenarSecuencia")
    class GenerateOrdenarSecuencia {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating ordenar secuencia Then returns OrdenarSecuenciaResponseDto")
        void shouldGenerateOrdenarSecuenciaSuccessfully() throws IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            OrdenarSecuenciaRequestDto requestDto = OrdenarSecuenciaTestMother.validOrdenarSecuenciaRequestDto();
            OrdenarSecuencia savedOrdenarSecuencia = OrdenarSecuenciaTestMother.savedOrdenarSecuencia(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );
            List<Event> events = OrdenarSecuenciaTestMother.savedEvents(savedOrdenarSecuencia, 3);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(validateEvents).validate(any());
            when(eventsGenerateService.generateList(any(), any())).thenReturn(events);
            when(ordenarSecuenciaRepository.save(any(OrdenarSecuencia.class))).thenReturn(savedOrdenarSecuencia);
            when(transactionGenerateService.generate(
                any(TypeTransaction.class),
                any(Double.class),
                any(String.class),
                any(TransactionActor.class),
                any(TransactionActor.class),
                any(),
                any(Subject.class),
                any(OrdenarSecuencia.class),
                any(),
                any(),
                any(),
                any()
            )).thenReturn(mock(trinity.play2learn.backend.economy.transaction.models.Transaction.class));

            // When
            OrdenarSecuenciaResponseDto response = ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(requestDto, user);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(ActivityTestMother.ACTIVITY_ID);
            assertThat(response.getName()).isEqualTo("Ordenar Secuencia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateEvents).validate(requestDto.getEvents());
            verify(eventsGenerateService).generateList(anyList(), any(OrdenarSecuencia.class));
            verify(ordenarSecuenciaRepository).save(any(OrdenarSecuencia.class));
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(OrdenarSecuenciaTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad de ordenar secuencia"),
                eq(TransactionActor.SISTEMA),
                eq(TransactionActor.SISTEMA),
                eq(null),
                eq(subject),
                eq(savedOrdenarSecuencia),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
            );
        }

        @Test
        @DisplayName("Given subject not found When generating ordenar secuencia Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() throws IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            OrdenarSecuenciaRequestDto requestDto = OrdenarSecuenciaTestMother.validOrdenarSecuenciaRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("La materia no fue encontrada"));

            // When & Then
            assertThatThrownBy(() -> ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("La materia no fue encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateEvents, never()).validate(any());
            verify(eventsGenerateService, never()).generateList(any(), any());
            verify(ordenarSecuenciaRepository, never()).save(any());
            verify(transactionGenerateService, never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating ordenar secuencia Then throws ConflictException")
        void shouldThrowConflictExceptionWhenTeacherNotAssigned() throws IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Teacher otherTeacher = ActivityTestMother.teacher(500L, "other@example.com");
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                otherTeacher
            );
            OrdenarSecuenciaRequestDto requestDto = OrdenarSecuenciaTestMother.validOrdenarSecuenciaRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateEvents, never()).validate(any());
            verify(eventsGenerateService, never()).generateList(any(), any());
            verify(ordenarSecuenciaRepository, never()).save(any());
            verify(transactionGenerateService, never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given invalid events order When generating ordenar secuencia Then throws BadRequestException")
        void shouldThrowBadRequestExceptionWhenInvalidEventsOrder() throws IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            OrdenarSecuenciaRequestDto requestDto = OrdenarSecuenciaTestMother.validOrdenarSecuenciaRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doThrow(new BadRequestException("Los ordenes de los eventos no son consecutivos"))
                .when(validateEvents).validate(any());

            // When & Then
            assertThatThrownBy(() -> ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Los ordenes de los eventos no son consecutivos");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(subjectGetService).findById(ActivityTestMother.SUBJECT_ID);
            verify(validateEvents).validate(requestDto.getEvents());
            verify(eventsGenerateService, never()).generateList(any(), any());
            verify(ordenarSecuenciaRepository, never()).save(any());
            verify(transactionGenerateService, never()).generate(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Given valid request When generating ordenar secuencia Then verifies saved fields with ArgumentCaptor")
        void shouldVerifySavedFields() throws IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            OrdenarSecuenciaRequestDto requestDto = OrdenarSecuenciaTestMother.validOrdenarSecuenciaRequestDto();
            OrdenarSecuencia savedOrdenarSecuencia = OrdenarSecuenciaTestMother.savedOrdenarSecuencia(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );
            List<Event> events = OrdenarSecuenciaTestMother.savedEvents(savedOrdenarSecuencia, 3);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(subjectGetService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            doNothing().when(validateEvents).validate(any());
            when(eventsGenerateService.generateList(any(), any())).thenReturn(events);
            when(ordenarSecuenciaRepository.save(any(OrdenarSecuencia.class))).thenReturn(savedOrdenarSecuencia);
            when(transactionGenerateService.generate(
                any(TypeTransaction.class),
                any(Double.class),
                any(String.class),
                any(TransactionActor.class),
                any(TransactionActor.class),
                any(),
                any(Subject.class),
                any(OrdenarSecuencia.class),
                any(),
                any(),
                any(),
                any()
            )).thenReturn(mock(trinity.play2learn.backend.economy.transaction.models.Transaction.class));

            // When
            ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(requestDto, user);

            // Then
            ArgumentCaptor<OrdenarSecuencia> ordenarSecuenciaCaptor = ArgumentCaptor.forClass(OrdenarSecuencia.class);
            verify(ordenarSecuenciaRepository).save(ordenarSecuenciaCaptor.capture());

            OrdenarSecuencia capturedOrdenarSecuencia = ordenarSecuenciaCaptor.getValue();
            assertThat(capturedOrdenarSecuencia)
                .extracting(
                    OrdenarSecuencia::getName,
                    OrdenarSecuencia::getDescription,
                    OrdenarSecuencia::getSubject,
                    OrdenarSecuencia::getInitialBalance
                )
                .containsExactly(
                    "Ordenar Secuencia",
                    requestDto.getDescription(),
                    subject,
                    OrdenarSecuenciaTestMother.DEFAULT_INITIAL_BALANCE
                );
        }
    }
}

