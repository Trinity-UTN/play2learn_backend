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

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitListByTeacherServiceTest {

    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IBenefitRepository benefitRepository;
    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private BenefitListByTeacherService benefitListByTeacherService;

    @BeforeEach
    void setUp() {
        benefitListByTeacherService = new BenefitListByTeacherService(benefitRepository, teacherGetByEmailService);
    }

    @Nested
    @DisplayName("cu55ListBenefitsByTeacher")
    class ListBenefitsByTeacher {

        @Test
        @DisplayName("Given teacher with benefits When listing Then returns list of benefit DTOs")
        void whenTeacherHasBenefits_returnsListOfBenefits() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            List<Benefit> benefits = List.of(benefit1, benefit2);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitRepository.findAllBySubjectTeacherAndDeletedAtIsNull(teacher)).thenReturn(benefits);

            // When
            List<BenefitResponseDto> result = benefitListByTeacherService.cu55ListBenefitsByTeacher(user);

            // Then
            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(benefitRepository).findAllBySubjectTeacherAndDeletedAtIsNull(teacher);
            assertThat(result).hasSize(2);
            assertThat(result).extracting(BenefitResponseDto::getId).containsExactly(1001L, 1002L);
        }

        @Test
        @DisplayName("Given teacher without benefits When listing Then returns empty list")
        void whenTeacherHasNoBenefits_returnsEmptyList() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitRepository.findAllBySubjectTeacherAndDeletedAtIsNull(teacher)).thenReturn(List.of());

            // When
            List<BenefitResponseDto> result = benefitListByTeacherService.cu55ListBenefitsByTeacher(user);

            // Then
            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(benefitRepository).findAllBySubjectTeacherAndDeletedAtIsNull(teacher);
            assertThat(result).isEmpty();
        }
    }
}

