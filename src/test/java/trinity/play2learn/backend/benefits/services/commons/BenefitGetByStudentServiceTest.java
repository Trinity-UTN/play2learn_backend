package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByStudentService;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;

@ExtendWith(MockitoExtension.class)
class BenefitGetByStudentServiceTest {

    @Mock
    private ISubjectGetByStudentService subjectGetByStudentService;
    @Mock
    private IBenefitRepository benefitRepository;

    private BenefitGetByStudentService benefitGetByStudentService;

    @BeforeEach
    void setUp() {
        benefitGetByStudentService = new BenefitGetByStudentService(subjectGetByStudentService, benefitRepository);
    }

    @Nested
    @DisplayName("getByStudent")
    class GetByStudent {

        @Test
        @DisplayName("Given student with subjects When getting benefits Then returns benefits for student's subjects")
        void whenStudentHasSubjects_returnsBenefits() {
            // Given
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            List<Subject> subjects = List.of(subject);
            List<Benefit> expectedBenefits = List.of(benefit);

            when(subjectGetByStudentService.getByStudent(student)).thenReturn(subjects);
            when(benefitRepository.findBySubjectIn(subjects)).thenReturn(expectedBenefits);

            // When
            List<Benefit> result = benefitGetByStudentService.getByStudent(student);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result).contains(benefit);
            verify(subjectGetByStudentService).getByStudent(student);
            verify(benefitRepository).findBySubjectIn(subjects);
        }

        @Test
        @DisplayName("Given student with no subjects When getting benefits Then returns empty list")
        void whenStudentHasNoSubjects_returnsEmptyList() {
            // Given
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            List<Subject> subjects = new ArrayList<>();
            List<Benefit> expectedBenefits = new ArrayList<>();

            when(subjectGetByStudentService.getByStudent(student)).thenReturn(subjects);
            when(benefitRepository.findBySubjectIn(subjects)).thenReturn(expectedBenefits);

            // When
            List<Benefit> result = benefitGetByStudentService.getByStudent(student);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(subjectGetByStudentService).getByStudent(student);
            verify(benefitRepository).findBySubjectIn(subjects);
        }

        @Test
        @DisplayName("Given student with multiple subjects When getting benefits Then returns all benefits")
        void whenStudentHasMultipleSubjects_returnsAllBenefits() {
            // Given
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject1 = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Subject subject2 = BenefitTestMother.subjectWithTeacher(
                202L,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit1 = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject1);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject2);
            List<Subject> subjects = List.of(subject1, subject2);
            List<Benefit> expectedBenefits = List.of(benefit1, benefit2);

            when(subjectGetByStudentService.getByStudent(student)).thenReturn(subjects);
            when(benefitRepository.findBySubjectIn(subjects)).thenReturn(expectedBenefits);

            // When
            List<Benefit> result = benefitGetByStudentService.getByStudent(student);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).contains(benefit1, benefit2);
            verify(subjectGetByStudentService).getByStudent(student);
            verify(benefitRepository).findBySubjectIn(subjects);
        }
    }
}

