package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.subject;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.teacher;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;

@ExtendWith(MockitoExtension.class)
class SubjectGetServiceTest {

    private static final long SUBJECT_ID = 501L;

    @Mock
    private ISubjectGetByIdService getByIdService;

    private SubjectGetService subjectGetService;

    @BeforeEach
    void setUp() {
        subjectGetService = new SubjectGetService(getByIdService);
    }

    @Nested
    @DisplayName("Get subject by id")
    class GetSubjectById {

        @Test
        @DisplayName("Given existing subject id When getting Then maps domain entity to DTO")
        void returnsMappedDto() {
            Subject subject = existingSubject();
            when(getByIdService.findById(SUBJECT_ID)).thenReturn(subject);

            SubjectResponseDto dto = subjectGetService.cu33GetSubjectById(SUBJECT_ID);

            verify(getByIdService).findById(SUBJECT_ID);
            assertThat(dto)
                .as("DTO should mirror id, name and course data")
                .satisfies(result -> {
                    assertThat(result.getId()).isEqualTo(SUBJECT_ID);
                    assertThat(result.getName()).isEqualTo(subject.getName());
                    assertThat(result.getCourse().getId()).isEqualTo(subject.getCourse().getId());
                });
        }

        private Subject existingSubject() {
            return subject(SUBJECT_ID, "Robotica", course(1L), teacher(1L), new ArrayList<>());
        }
    }
}
