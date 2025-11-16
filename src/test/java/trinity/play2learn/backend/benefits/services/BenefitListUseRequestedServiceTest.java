package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitListUseRequestedServiceTest {

    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;
    @Mock
    private IBenefitRepository benefitRepository;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitListUseRequestedService benefitListUseRequestedService;

    @BeforeEach
    void setUp() {
        benefitListUseRequestedService = new BenefitListUseRequestedService(
            teacherGetByEmailService,
            benefitRepository,
            benefitPurchaseRepository
        );
    }

    @Nested
    @DisplayName("cu82ListUseRequestedByTeacher")
    class ListUseRequestedByTeacher {

        @Test
        @DisplayName("Given teacher with use requested purchases When listing Then returns list of use requested purchases")
        void whenTeacherHasUseRequestedPurchases_returnsUseRequestedPurchases() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            List<Benefit> teacherBenefits = List.of(benefit1, benefit2);

            Student student1 = BenefitTestMother.student(401L, "student1@example.com");
            Student student2 = BenefitTestMother.student(402L, "student2@example.com");
            BenefitPurchase purchase1 = BenefitTestMother.useRequestedBenefitPurchase(benefit1, student1);
            BenefitPurchase purchase2 = BenefitTestMother.useRequestedBenefitPurchase(benefit2, student2);
            List<BenefitPurchase> useRequested1 = List.of(purchase1);
            List<BenefitPurchase> useRequested2 = List.of(purchase2);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitRepository.findAllBySubjectTeacherAndDeletedAtIsNull(teacher)).thenReturn(teacherBenefits);
            when(benefitPurchaseRepository.findAllByBenefitAndState(benefit1, BenefitPurchaseState.USE_REQUESTED)).thenReturn(useRequested1);
            when(benefitPurchaseRepository.findAllByBenefitAndState(benefit2, BenefitPurchaseState.USE_REQUESTED)).thenReturn(useRequested2);

            // When
            List<BenefitPurchaseSimpleResponseDto> result = benefitListUseRequestedService.cu82ListUseRequestedByTeacher(user);

            // Then
            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(benefitRepository).findAllBySubjectTeacherAndDeletedAtIsNull(teacher);
            verify(benefitPurchaseRepository).findAllByBenefitAndState(benefit1, BenefitPurchaseState.USE_REQUESTED);
            verify(benefitPurchaseRepository).findAllByBenefitAndState(benefit2, BenefitPurchaseState.USE_REQUESTED);
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Given teacher with expired or deleted benefits When listing Then skips those benefits")
        void whenTeacherHasExpiredOrDeletedBenefits_skipsThoseBenefits() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit activeBenefit = BenefitTestMother.benefit(1001L, subject);
            Benefit expiredBenefit = BenefitTestMother.expiredBenefit(1002L, subject);
            Benefit deletedBenefit = BenefitTestMother.deletedBenefit(1003L, subject);
            List<Benefit> teacherBenefits = List.of(activeBenefit, expiredBenefit, deletedBenefit);

            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchase = BenefitTestMother.useRequestedBenefitPurchase(activeBenefit, student);
            List<BenefitPurchase> useRequested = List.of(purchase);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitRepository.findAllBySubjectTeacherAndDeletedAtIsNull(teacher)).thenReturn(teacherBenefits);
            when(benefitPurchaseRepository.findAllByBenefitAndState(activeBenefit, BenefitPurchaseState.USE_REQUESTED)).thenReturn(useRequested);

            // When
            List<BenefitPurchaseSimpleResponseDto> result = benefitListUseRequestedService.cu82ListUseRequestedByTeacher(user);

            // Then
            verify(benefitPurchaseRepository).findAllByBenefitAndState(activeBenefit, BenefitPurchaseState.USE_REQUESTED);
            verify(benefitPurchaseRepository, never()).findAllByBenefitAndState(expiredBenefit, BenefitPurchaseState.USE_REQUESTED);
            verify(benefitPurchaseRepository, never()).findAllByBenefitAndState(deletedBenefit, BenefitPurchaseState.USE_REQUESTED);
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Given teacher with no use requested purchases When listing Then returns empty list")
        void whenTeacherHasNoUseRequestedPurchases_returnsEmptyList() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(1001L, subject);
            List<Benefit> teacherBenefits = List.of(benefit);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitRepository.findAllBySubjectTeacherAndDeletedAtIsNull(teacher)).thenReturn(teacherBenefits);
            when(benefitPurchaseRepository.findAllByBenefitAndState(benefit, BenefitPurchaseState.USE_REQUESTED)).thenReturn(List.of());

            // When
            List<BenefitPurchaseSimpleResponseDto> result = benefitListUseRequestedService.cu82ListUseRequestedByTeacher(user);

            // Then
            verify(benefitPurchaseRepository).findAllByBenefitAndState(benefit, BenefitPurchaseState.USE_REQUESTED);
            assertThat(result).isEmpty();
        }
    }
}

