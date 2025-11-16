package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitGenerateServiceTest {

    @Mock
    private IBenefitRepository benefitRepository;
    @Mock
    private ISubjectGetByIdService subjectGetService;

    private BenefitGenerateService benefitGenerateService;

    @BeforeEach
    void setUp() {
        benefitGenerateService = new BenefitGenerateService(benefitRepository, subjectGetService);
    }

    @Nested
    @DisplayName("cu51GenerateBenefit")
    class GenerateBenefit {

        @Test
        @DisplayName("Given valid benefit request and authorized teacher When generating Then creates and persists benefit")
        void whenRequestValidAndTeacherAuthorized_persistsBenefit() {
            // Given
            BenefitRequestDto request = BenefitTestMother.benefitRequestBuilder(BenefitTestMother.DEFAULT_SUBJECT_ID).build();
            User user = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                teacher
            );
            Benefit savedBenefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(subjectGetService.findById(BenefitTestMother.DEFAULT_SUBJECT_ID)).thenReturn(subject);
            when(benefitRepository.save(any(Benefit.class))).thenReturn(savedBenefit);

            // When
            BenefitResponseDto response = benefitGenerateService.cu51GenerateBenefit(request, user);

            // Then
            ArgumentCaptor<Benefit> benefitCaptor = ArgumentCaptor.forClass(Benefit.class);
            verify(subjectGetService).findById(BenefitTestMother.DEFAULT_SUBJECT_ID);
            verify(benefitRepository).save(benefitCaptor.capture());

            Benefit capturedBenefit = benefitCaptor.getValue();
            assertThat(capturedBenefit.getName()).isEqualTo(request.getName());
            assertThat(capturedBenefit.getDescription()).isEqualTo(request.getDescription());
            assertThat(capturedBenefit.getCost()).isEqualTo(request.getCost());
            assertThat(capturedBenefit.getSubject()).isEqualTo(subject);
            assertThat(capturedBenefit.getIcon()).isEqualTo(request.getIcon());
            assertThat(capturedBenefit.getCategory()).isEqualTo(request.getCategory());
            assertThat(capturedBenefit.getColor()).isEqualTo(request.getColor());

            assertThat(response.getId()).isEqualTo(savedBenefit.getId());
            assertThat(response.getName()).isEqualTo(savedBenefit.getName());
        }

        @Test
        @DisplayName("Given subject is missing When generating benefit Then propagates NotFoundException and stops flow")
        void whenSubjectMissing_propagatesNotFound() {
            // Given
            BenefitRequestDto request = BenefitTestMother.benefitRequestBuilder(BenefitTestMother.DEFAULT_SUBJECT_ID).build();
            User user = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_TEACHER_EMAIL);

            when(subjectGetService.findById(BenefitTestMother.DEFAULT_SUBJECT_ID))
                .thenThrow(new NotFoundException("Materia no encontrada"));

            // When & Then
            assertThatThrownBy(() -> benefitGenerateService.cu51GenerateBenefit(request, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Materia no encontrada");

            verify(subjectGetService).findById(BenefitTestMother.DEFAULT_SUBJECT_ID);
            verifyNoInteractions(benefitRepository);
        }

        @Test
        @DisplayName("Given teacher is not assigned to subject When generating benefit Then throws UnauthorizedException and avoids persistence")
        void whenTeacherNotAssignedToSubject_throwsUnauthorized() {
            // Given
            BenefitRequestDto request = BenefitTestMother.benefitRequestBuilder(BenefitTestMother.DEFAULT_SUBJECT_ID).build();
            User unauthorizedUser = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_UNAUTHORIZED_TEACHER_EMAIL);
            Teacher authorizedTeacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                authorizedTeacher
            );

            when(subjectGetService.findById(BenefitTestMother.DEFAULT_SUBJECT_ID)).thenReturn(subject);

            // When & Then
            assertThatThrownBy(() -> benefitGenerateService.cu51GenerateBenefit(request, unauthorizedUser))
                .isInstanceOf(UnauthorizedException.class);

            verify(subjectGetService).findById(BenefitTestMother.DEFAULT_SUBJECT_ID);
            verify(benefitRepository, never()).save(any(Benefit.class));
        }

        @Test
        @DisplayName("Given benefit with unlimited purchases When generating Then creates benefit with null purchase limits")
        void whenUnlimitedPurchases_createsBenefitWithNullLimits() {
            // Given
            BenefitRequestDto request = BenefitTestMother.benefitRequestBuilder(BenefitTestMother.DEFAULT_SUBJECT_ID)
                .purchaseLimit(null)
                .purchaseLimitPerStudent(null)
                .build();
            User user = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                teacher
            );
            Benefit savedBenefit = BenefitTestMother.unlimitedBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(subjectGetService.findById(BenefitTestMother.DEFAULT_SUBJECT_ID)).thenReturn(subject);
            when(benefitRepository.save(any(Benefit.class))).thenReturn(savedBenefit);

            // When
            benefitGenerateService.cu51GenerateBenefit(request, user);

            // Then
            ArgumentCaptor<Benefit> benefitCaptor = ArgumentCaptor.forClass(Benefit.class);
            verify(benefitRepository).save(benefitCaptor.capture());

            Benefit capturedBenefit = benefitCaptor.getValue();
            assertThat(capturedBenefit.getPurchaseLimit()).isNull();
            assertThat(capturedBenefit.getPurchaseLimitPerStudent()).isNull();
        }
    }
}

