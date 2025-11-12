package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.subject;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.teacher;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;

@ExtendWith(MockitoExtension.class)
class SubjectListServiceTest {

    @Mock
    private ISubjectRepository subjectRepository;

    private SubjectListService subjectListService;

    @BeforeEach
    void setUp() {
        subjectListService = new SubjectListService(subjectRepository);
    }

    @Nested
    @DisplayName("List all subjects")
    class ListSubjects {

        @Test
        @DisplayName("When listing subjects Then delegates to repository ignoring deleted ones and maps DTOs")
        void listSubjects_returnsMappedDtos() {
            List<Subject> subjects = List.of(
                subject(1L, "Robotica", course(1L), teacher(1L), new ArrayList<>()),
                subject(2L, "IA", course(2L), teacher(2L), new ArrayList<>())
            );
            when(subjectRepository.findAllByDeletedAtIsNull()).thenReturn(subjects);

            List<SubjectResponseDto> result = subjectListService.cu31ListSubjects();

            verify(subjectRepository).findAllByDeletedAtIsNull();
            assertThat(result)
                .as("should map every active subject to DTO")
                .hasSize(2)
                .extracting(SubjectResponseDto::getId)
                .containsExactly(1L, 2L);
        }
    }
}
