package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.student;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.students;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.subject;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.teacher;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsGetByIdListService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectAddResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;

@ExtendWith(MockitoExtension.class)
class SubjectAddStudentsServiceTest {

    private static final long SUBJECT_ID = 90L;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ISubjectGetByIdService subjectGetService;

    @Mock
    private IStudentsGetByIdListService studentsGetByListService;

    private SubjectAddStudentsService subjectAddStudentsService;

    @BeforeEach
    void setUp() {
        subjectAddStudentsService = new SubjectAddStudentsService(
            subjectRepository,
            subjectGetService,
            studentsGetByListService
        );
    }

    @Nested
    @DisplayName("Add students to subject")
    class AddStudents {

        @Test
        @DisplayName("Given subject without targeted students When adding Then appends new students and returns updated DTO")
        void addsNewStudents() {
            Subject subject = subjectWithStudents(SUBJECT_ID, students(1L));
            List<Student> studentsToAdd = students(2L, 3L);

            when(subjectGetService.findById(SUBJECT_ID)).thenReturn(subject);
            when(studentsGetByListService.getStudentsByIdList(List.of(2L, 3L))).thenReturn(studentsToAdd);
            when(subjectRepository.save(subject)).thenReturn(subject);

            SubjectAddResponseDto response = subjectAddStudentsService.cu36AddStudentsToSubject(
                SUBJECT_ID,
                List.of(2L, 3L)
            );

            Subject savedSubject = captureSavedSubject();
            assertThat(savedSubject.getStudents())
                .as("should merge existing and new students without duplicates")
                .extracting(Student::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);

            assertThat(response.getId()).isEqualTo(subject.getId());
            assertThat(response.getStudents()).hasSize(3);
        }

        @Test
        @DisplayName("Given subject already containing a student When adding same student Then avoids duplicates")
        void avoidsDuplicates() {
            Subject subject = subjectWithStudents(SUBJECT_ID, students(1L, 2L));
            Student existingSecond = subject.getStudents().get(1);
            List<Student> duplicatedStudents = List.of(existingSecond, student(3L));

            when(subjectGetService.findById(SUBJECT_ID)).thenReturn(subject);
            when(studentsGetByListService.getStudentsByIdList(List.of(2L, 3L))).thenReturn(duplicatedStudents);
            when(subjectRepository.save(subject)).thenReturn(subject);

            subjectAddStudentsService.cu36AddStudentsToSubject(SUBJECT_ID, List.of(2L, 3L));

            assertThat(subject.getStudents())
                .as("existing student should not be duplicated")
                .extracting(Student::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
        }

        @Test
        @DisplayName("Given empty list of students When adding Then persists without modifications")
        void addingEmptyListDoesNothing() {
            Subject subject = subjectWithStudents(SUBJECT_ID, students(1L));

            when(subjectGetService.findById(SUBJECT_ID)).thenReturn(subject);
            when(studentsGetByListService.getStudentsByIdList(List.of())).thenReturn(List.of());
            when(subjectRepository.save(subject)).thenReturn(subject);

            subjectAddStudentsService.cu36AddStudentsToSubject(SUBJECT_ID, List.of());

            assertThat(subject.getStudents())
                .as("no students should be added when the list is empty")
                .extracting(Student::getId)
                .containsExactly(1L);
        }

        private Subject captureSavedSubject() {
            ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
            verify(subjectRepository).save(subjectCaptor.capture());
            return subjectCaptor.getValue();
        }
    }

    private Subject subjectWithStudents(Long subjectId, List<Student> currentStudents) {
        return subject(
            subjectId,
            "Fisica",
            course(5L),
            teacher(50L),
            new ArrayList<>(currentStudents)
        );
    }
}

