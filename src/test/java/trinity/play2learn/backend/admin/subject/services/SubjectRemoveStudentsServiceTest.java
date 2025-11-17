package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
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
class SubjectRemoveStudentsServiceTest {

    private static final long SUBJECT_ID = 120L;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ISubjectGetByIdService findSubjectByIdService;

    @Mock
    private IStudentsGetByIdListService studentListService;

    private SubjectRemoveStudentsService subjectRemoveStudentsService;

    @BeforeEach
    void setUp() {
        subjectRemoveStudentsService = new SubjectRemoveStudentsService(
            subjectRepository,
            findSubjectByIdService,
            studentListService
        );
    }

    @Nested
    @DisplayName("Remove students from subject")
    class RemoveStudents {

        @Test
        @DisplayName("Given subject with matching students When removing Then removes each student and returns DTO")
        void removesExistingStudents() {
            Subject subject = subjectWithStudents(SUBJECT_ID, students(1L, 2L, 3L));
            Student second = subject.getStudents().get(1);
            Student third = subject.getStudents().get(2);
            List<Student> studentsToRemove = List.of(second, third);

            when(findSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subject);
            when(studentListService.getStudentsByIdList(List.of(2L, 3L))).thenReturn(studentsToRemove);
            when(subjectRepository.save(subject)).thenReturn(subject);

            SubjectAddResponseDto response = subjectRemoveStudentsService.cu37RemoveStudentsFromSubject(
                SUBJECT_ID,
                List.of(2L, 3L)
            );

            Subject persistedSubject = captureSavedSubject();
            assertThat(persistedSubject.getStudents())
                .as("students removed should be absent from subject")
                .extracting(Student::getId)
                .containsExactly(1L);

            assertThat(response.getStudents()).hasSize(1);
        }

        @Test
        @DisplayName("Given subject without requested students When removing Then leaves collection unchanged")
        void removingNonexistentStudentsDoesNothing() {
            Subject subject = subjectWithStudents(SUBJECT_ID, students(1L));

            when(findSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subject);
            when(studentListService.getStudentsByIdList(List.of(5L))).thenReturn(students(5L));
            when(subjectRepository.save(subject)).thenReturn(subject);

            subjectRemoveStudentsService.cu37RemoveStudentsFromSubject(SUBJECT_ID, List.of(5L));

            assertThat(subject.getStudents())
                .as("non-existent students should not alter the collection")
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
            "Historia del Arte",
            course(7L),
            teacher(70L),
            new ArrayList<>(currentStudents)
        );
    }
}

