package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

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
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterStrategyService;

@ExtendWith(MockitoExtension.class)
class BenefitFilterByStudentStateServiceTest {

    @Mock
    private Map<String, IBenefitFilterStrategyService> benefitFilterByStudentStateServiceMap;
    @Mock
    private IBenefitFilterStrategyService filterStrategyService;

    private BenefitFilterByStudentStateService benefitFilterByStudentStateService;

    @BeforeEach
    void setUp() {
        benefitFilterByStudentStateService = new BenefitFilterByStudentStateService(
            benefitFilterByStudentStateServiceMap
        );
    }

    @Nested
    @DisplayName("filterByStudentState")
    class FilterByStudentState {

        @Test
        @DisplayName("Given benefits and student with AVAILABLE state When filtering Then delegates to AVAILABLE strategy")
        void whenAvailableState_delegatesToAvailableStrategy() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of(benefit);
            List<Benefit> filteredBenefits = List.of(benefit);

            when(benefitFilterByStudentStateServiceMap.get(BenefitStudentState.AVAILABLE.name()))
                .thenReturn(filterStrategyService);
            when(filterStrategyService.filter(benefits, student)).thenReturn(filteredBenefits);

            // When
            List<Benefit> result = benefitFilterByStudentStateService.filterByStudentState(
                benefits,
                student,
                BenefitStudentState.AVAILABLE
            );

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result).contains(benefit);
            verify(benefitFilterByStudentStateServiceMap).get(BenefitStudentState.AVAILABLE.name());
            verify(filterStrategyService).filter(benefits, student);
        }

        @Test
        @DisplayName("Given benefits and student with PURCHASED state When filtering Then delegates to PURCHASED strategy")
        void whenPurchasedState_delegatesToPurchasedStrategy() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of(benefit);
            List<Benefit> filteredBenefits = List.of(benefit);

            when(benefitFilterByStudentStateServiceMap.get(BenefitStudentState.PURCHASED.name()))
                .thenReturn(filterStrategyService);
            when(filterStrategyService.filter(benefits, student)).thenReturn(filteredBenefits);

            // When
            List<Benefit> result = benefitFilterByStudentStateService.filterByStudentState(
                benefits,
                student,
                BenefitStudentState.PURCHASED
            );

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result).contains(benefit);
            verify(benefitFilterByStudentStateServiceMap).get(BenefitStudentState.PURCHASED.name());
            verify(filterStrategyService).filter(benefits, student);
        }

        @Test
        @DisplayName("Given benefits and student with USE_REQUESTED state When filtering Then delegates to USE_REQUESTED strategy")
        void whenUseRequestedState_delegatesToUseRequestedStrategy() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of(benefit);
            List<Benefit> filteredBenefits = List.of(benefit);

            when(benefitFilterByStudentStateServiceMap.get(BenefitStudentState.USE_REQUESTED.name()))
                .thenReturn(filterStrategyService);
            when(filterStrategyService.filter(benefits, student)).thenReturn(filteredBenefits);

            // When
            List<Benefit> result = benefitFilterByStudentStateService.filterByStudentState(
                benefits,
                student,
                BenefitStudentState.USE_REQUESTED
            );

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result).contains(benefit);
            verify(benefitFilterByStudentStateServiceMap).get(BenefitStudentState.USE_REQUESTED.name());
            verify(filterStrategyService).filter(benefits, student);
        }

        @Test
        @DisplayName("Given benefits and student with EXPIRED state When filtering Then delegates to EXPIRED strategy")
        void whenExpiredState_delegatesToExpiredStrategy() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Benefit> benefits = List.of(benefit);
            List<Benefit> filteredBenefits = List.of(benefit);

            when(benefitFilterByStudentStateServiceMap.get(BenefitStudentState.EXPIRED.name()))
                .thenReturn(filterStrategyService);
            when(filterStrategyService.filter(benefits, student)).thenReturn(filteredBenefits);

            // When
            List<Benefit> result = benefitFilterByStudentStateService.filterByStudentState(
                benefits,
                student,
                BenefitStudentState.EXPIRED
            );

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result).contains(benefit);
            verify(benefitFilterByStudentStateServiceMap).get(BenefitStudentState.EXPIRED.name());
            verify(filterStrategyService).filter(benefits, student);
        }
    }
}

