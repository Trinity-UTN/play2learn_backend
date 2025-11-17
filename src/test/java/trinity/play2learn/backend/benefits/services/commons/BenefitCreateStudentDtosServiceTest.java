package trinity.play2learn.backend.benefits.services.commons;

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
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetPurchasesLeftByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetStudentStateService;

@ExtendWith(MockitoExtension.class)
class BenefitCreateStudentDtosServiceTest {

    @Mock
    private IBenefitGetStudentStateService benefitGetStudentStateService;
    @Mock
    private IBenefitGetPurchasesLeftByStudentService benefitGetPurchasesLeftByStudentService;

    private BenefitCreateStudentDtosService benefitCreateStudentDtosService;

    @BeforeEach
    void setUp() {
        benefitCreateStudentDtosService = new BenefitCreateStudentDtosService(
            benefitGetStudentStateService,
            benefitGetPurchasesLeftByStudentService
        );
    }

    @Nested
    @DisplayName("createBenefitStudentDtos")
    class CreateBenefitStudentDtos {

        @Test
        @DisplayName("Given list of benefits and student When creating DTOs Then returns list of student DTOs")
        void whenBenefitsExist_returnsStudentDtos() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of(benefit);

            when(benefitGetStudentStateService.getStudentState(benefit, student))
                .thenReturn(BenefitStudentState.AVAILABLE);
            when(benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit, student))
                .thenReturn(1);

            // When
            List<BenefitStudentResponseDto> result = benefitCreateStudentDtosService.createBenefitStudentDtos(benefits, student);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            BenefitStudentResponseDto dto = result.get(0);
            assertThat(dto.getId()).isEqualTo(benefit.getId());
            assertThat(dto.getName()).isEqualTo(benefit.getName());
            assertThat(dto.getState()).isEqualTo(BenefitStudentState.AVAILABLE);
            assertThat(dto.getPurchasesLeftByStudent()).isEqualTo(1);
            verify(benefitGetStudentStateService).getStudentState(benefit, student);
            verify(benefitGetPurchasesLeftByStudentService).getPurchasesLeftByStudent(benefit, student);
        }

        @Test
        @DisplayName("Given empty list of benefits When creating DTOs Then returns empty list")
        void whenNoBenefits_returnsEmptyList() {
            // Given
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of();

            // When
            List<BenefitStudentResponseDto> result = benefitCreateStudentDtosService.createBenefitStudentDtos(benefits, student);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Given multiple benefits When creating DTOs Then returns DTOs for all benefits")
        void whenMultipleBenefits_returnsDtosForAll() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit1 = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of(benefit1, benefit2);

            when(benefitGetStudentStateService.getStudentState(benefit1, student))
                .thenReturn(BenefitStudentState.AVAILABLE);
            when(benefitGetStudentStateService.getStudentState(benefit2, student))
                .thenReturn(BenefitStudentState.PURCHASED);
            when(benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit1, student))
                .thenReturn(1);
            when(benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit2, student))
                .thenReturn(0);

            // When
            List<BenefitStudentResponseDto> result = benefitCreateStudentDtosService.createBenefitStudentDtos(benefits, student);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(benefit1.getId());
            assertThat(result.get(0).getState()).isEqualTo(BenefitStudentState.AVAILABLE);
            assertThat(result.get(1).getId()).isEqualTo(benefit2.getId());
            assertThat(result.get(1).getState()).isEqualTo(BenefitStudentState.PURCHASED);
            verify(benefitGetStudentStateService).getStudentState(benefit1, student);
            verify(benefitGetStudentStateService).getStudentState(benefit2, student);
            verify(benefitGetPurchasesLeftByStudentService).getPurchasesLeftByStudent(benefit1, student);
            verify(benefitGetPurchasesLeftByStudentService).getPurchasesLeftByStudent(benefit2, student);
        }

        @Test
        @DisplayName("Given benefit with null purchases left When creating DTOs Then sets null in DTO")
        void whenNullPurchasesLeft_setsNullInDto() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit unlimitedBenefit = BenefitTestMother.unlimitedBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of(unlimitedBenefit);

            when(benefitGetStudentStateService.getStudentState(unlimitedBenefit, student))
                .thenReturn(BenefitStudentState.AVAILABLE);
            when(benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(unlimitedBenefit, student))
                .thenReturn(null);

            // When
            List<BenefitStudentResponseDto> result = benefitCreateStudentDtosService.createBenefitStudentDtos(benefits, student);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPurchasesLeftByStudent()).isNull();
            verify(benefitGetStudentStateService).getStudentState(unlimitedBenefit, student);
            verify(benefitGetPurchasesLeftByStudentService).getPurchasesLeftByStudent(unlimitedBenefit, student);
        }
    }
}

