package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
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
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchasedUsedResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitListUsedByStudentServiceTest {

    private static final String STUDENT_EMAIL = "student@example.com";

    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;
    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    private BenefitListUsedByStudentService benefitListUsedByStudentService;

    @BeforeEach
    void setUp() {
        benefitListUsedByStudentService = new BenefitListUsedByStudentService(
            benefitPurchaseRepository,
            studentGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu93ListUsedByStudent")
    class ListUsedByStudent {

        @Test
        @DisplayName("Given student with used benefits When listing Then returns list of used purchases")
        void whenStudentHasUsedBenefits_returnsUsedPurchases() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            BenefitPurchase purchase1 = BenefitTestMother.usedBenefitPurchase(benefit1, student);
            BenefitPurchase purchase2 = BenefitTestMother.usedBenefitPurchase(benefit2, student);
            List<BenefitPurchase> usedPurchases = List.of(purchase1, purchase2);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitPurchaseRepository.findAllByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED))
                .thenReturn(usedPurchases);

            // When
            List<BenefitPurchasedUsedResponseDto> result = benefitListUsedByStudentService.cu93ListUsedByStudent(user);

            // Then
            verify(studentGetByEmailService).getByEmail(STUDENT_EMAIL);
            verify(benefitPurchaseRepository).findAllByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED);
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Given student with no used benefits When listing Then returns empty list")
        void whenStudentHasNoUsedBenefits_returnsEmptyList() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitPurchaseRepository.findAllByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED))
                .thenReturn(List.of());

            // When
            List<BenefitPurchasedUsedResponseDto> result = benefitListUsedByStudentService.cu93ListUsedByStudent(user);

            // Then
            verify(benefitPurchaseRepository).findAllByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED);
            assertThat(result).isEmpty();
        }
    }
}

