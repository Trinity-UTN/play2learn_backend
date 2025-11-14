package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectPaginatedRepository;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class SubjectListPaginatedServiceTest {

    @Mock
    private ISubjectPaginatedRepository subjectPaginatedRepository;

    private SubjectListPaginatedService subjectListPaginatedService;

    @BeforeEach
    void setUp() {
        subjectListPaginatedService = new SubjectListPaginatedService(subjectPaginatedRepository);
    }

    @Nested
    @DisplayName("List subjects paginated")
    class ListSubjectsPaginated {

        @Test
        @DisplayName("Given search and filters When listing Then applies pagination and maps results")
        void listSubjectsPaginated_withSearchAndFilters() {
            Subject subject = subject(1L, "Robotica", course(1L), teacher(5L), new ArrayList<>());
            when(subjectPaginatedRepository.findAll(any(), any(Pageable.class))).thenReturn(pageWith(subject, 40));

            PaginatedData<SubjectResponseDto> result = subjectListPaginatedService.cu32ListSubjectsPaginated(
                2,
                20,
                "name",
                "desc",
                "Rob",
                List.of("teacherId"),
                List.of("5")
            );

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(subjectPaginatedRepository).findAll(
                argThat((Specification<Subject> spec) -> spec != null),
                pageableCaptor.capture()
            );

            assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
            assertThat(result.getResults())
                .as("DTO list should contain mapped subject")
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.getId()).isEqualTo(1L);
                    assertThat(dto.getName()).isEqualTo("Robotica");
                });
            assertThat(result.getCount()).isEqualTo(40);
        }

        @Test
        @DisplayName("Given blank search and mismatched filters When listing Then applies base specification and maps page")
        void listSubjectsPaginated_withoutSearch_usesBaseSpec() {
            Subject subject = subject(10L, "IA", course(2L), teacher(6L), new ArrayList<>());
            when(subjectPaginatedRepository.findAll(any(), any(Pageable.class))).thenReturn(pageWith(subject, 10));

            PaginatedData<SubjectResponseDto> result = subjectListPaginatedService.cu32ListSubjectsPaginated(
                0,
                10,
                "id",
                "asc",
                null,
                List.of("teacherId"),
                List.of("7")
            );

            verify(subjectPaginatedRepository).findAll(
                argThat((Specification<Subject> spec) -> spec != null),
                any(Pageable.class)
            );

            assertThat(result.getResults())
                .as("DTO list should map content even when filters don't match")
                .singleElement()
                .extracting(SubjectResponseDto::getId)
                .isEqualTo(10L);
            assertThat(result.getTotalPages()).isEqualTo(1);
        }

        private Page<Subject> pageWith(Subject subject, long totalElements) {
            Pageable pageable = PaginatorUtils.buildPageable(0, 10, "name", "asc");
            return new PageImpl<>(List.of(subject), pageable, totalElements);
        }
    }
}
