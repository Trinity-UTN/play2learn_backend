package trinity.play2learn.backend.activity.memorama.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import trinity.play2learn.backend.activity.memorama.MemoramaTestMother;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.models.CouplesMemorama;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.repositories.IMemoramaRepository;
import trinity.play2learn.backend.activity.memorama.services.interfaces.ICouplesMemoramaGenerateService;
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
class MemoramaGenerateServiceTest {

    private static final Long TEACHER_ID = 400L;
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IMemoramaRepository memoramaRepository;

    @Mock
    private ISubjectGetByIdService findSubjectByIdService;

    @Mock
    private ICouplesMemoramaGenerateService couplesMemoramaGenerateService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private MemoramaGenerateService memoramaGenerateService;

    @BeforeEach
    void setUp() {
        memoramaGenerateService = new MemoramaGenerateService(
            findSubjectByIdService,
            memoramaRepository,
            couplesMemoramaGenerateService,
            transactionGenerateService,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu41GenerateMemorama")
    class GenerateMemorama {

        @Test
        @DisplayName("Given valid request with teacher assigned to subject When generating memorama Then returns MemoramaResponseDto")
        void shouldGenerateMemoramaSuccessfully() throws BadRequestException, IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            MemoramaRequestDto requestDto = MemoramaTestMother.validMemoramaRequestDto();
            Memorama savedMemorama = MemoramaTestMother.savedMemorama(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );
            List<CouplesMemorama> couples = MemoramaTestMother.savedCouples(savedMemorama, 4);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(memoramaRepository.save(any(Memorama.class))).thenReturn(savedMemorama);
            when(couplesMemoramaGenerateService.registerList(any(), any())).thenReturn(couples);
            when(memoramaRepository.save(savedMemorama)).thenReturn(savedMemorama);

            // When
            MemoramaResponseDto result = memoramaGenerateService.cu41GenerateMemorama(requestDto, user);

            // Then
            assertThat(result)
                .isNotNull()
                .extracting(
                    MemoramaResponseDto::getId,
                    MemoramaResponseDto::getName
                )
                .containsExactly(
                    savedMemorama.getId(),
                    "Memorama"
                );

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(findSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(memoramaRepository, times(2)).save(any(Memorama.class));
            verify(couplesMemoramaGenerateService).registerList(requestDto.getCouples(), savedMemorama);
            verify(memoramaRepository).save(savedMemorama);
            verify(transactionGenerateService).generate(
                eq(TypeTransaction.ACTIVIDAD),
                eq(MemoramaTestMother.DEFAULT_INITIAL_BALANCE),
                eq("Actividad de memorama"),
                eq(TransactionActor.SISTEMA),
                eq(TransactionActor.SISTEMA),
                eq(null),
                eq(subject),
                eq(savedMemorama),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
            );
        }

        @Test
        @DisplayName("Given subject not found When generating memorama Then throws NotFoundException")
        void shouldThrowNotFoundExceptionWhenSubjectNotFound() throws BadRequestException, IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            MemoramaRequestDto requestDto = MemoramaTestMother.validMemoramaRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID))
                .thenThrow(new NotFoundException("Materia no encontrada"));

            // When & Then
            assertThatThrownBy(() -> memoramaGenerateService.cu41GenerateMemorama(requestDto, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Materia no encontrada");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(findSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(memoramaRepository, never()).save(any(Memorama.class));
            verify(couplesMemoramaGenerateService, never()).registerList(any(), any());
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating memorama Then throws ConflictException")
        void shouldThrowConflictExceptionWhenTeacherNotAssigned() throws BadRequestException, IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Teacher otherTeacher = ActivityTestMother.teacher(500L, "other@example.com");
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                otherTeacher
            );
            MemoramaRequestDto requestDto = MemoramaTestMother.validMemoramaRequestDto();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> memoramaGenerateService.cu41GenerateMemorama(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El docente no esta asignado a la materia");

            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(findSubjectByIdService).findById(ActivityTestMother.SUBJECT_ID);
            verify(memoramaRepository, never()).save(any(Memorama.class));
            verify(couplesMemoramaGenerateService, never()).registerList(any(), any());
        }

        @Test
        @DisplayName("Given valid request When generating memorama Then saves memorama with correct fields")
        void shouldSaveMemoramaWithCorrectFields() throws BadRequestException, IOException {
            // Given
            User user = ActivityTestMother.teacherUser(TEACHER_ID, TEACHER_EMAIL);
            Teacher teacher = ActivityTestMother.teacher(TEACHER_ID, TEACHER_EMAIL);
            Subject subject = ActivityTestMother.subjectWithTeacher(
                ActivityTestMother.SUBJECT_ID,
                "Matemáticas",
                teacher
            );
            MemoramaRequestDto requestDto = MemoramaTestMother.validMemoramaRequestDto();
            Memorama savedMemorama = MemoramaTestMother.savedMemorama(
                ActivityTestMother.ACTIVITY_ID,
                subject
            );
            List<CouplesMemorama> couples = MemoramaTestMother.savedCouples(savedMemorama, 4);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(findSubjectByIdService.findById(ActivityTestMother.SUBJECT_ID)).thenReturn(subject);
            when(memoramaRepository.save(any(Memorama.class))).thenReturn(savedMemorama);
            when(couplesMemoramaGenerateService.registerList(any(), any())).thenReturn(couples);
            when(memoramaRepository.save(savedMemorama)).thenReturn(savedMemorama);

            // When
            memoramaGenerateService.cu41GenerateMemorama(requestDto, user);

            // Then
            ArgumentCaptor<Memorama> memoramaCaptor = ArgumentCaptor.forClass(Memorama.class);
            verify(memoramaRepository, times(2)).save(memoramaCaptor.capture());

            // Get the last captured value (second save call)
            Memorama capturedMemorama = memoramaCaptor.getAllValues().get(1);
            assertThat(capturedMemorama)
                .extracting(
                    Memorama::getName,
                    Memorama::getDescription,
                    Memorama::getSubject,
                    Memorama::getInitialBalance
                )
                .containsExactly(
                    "Memorama",
                    requestDto.getDescription(),
                    subject,
                    MemoramaTestMother.DEFAULT_INITIAL_BALANCE
                );
        }
    }
}

