package trinity.play2learn.backend.admin.subject.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListByTeacherService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListPaginatedService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class SubjectReadControllersTest {

    private MockMvc mockMvc;

    @Mock
    private ISubjectGetService subjectGetService;
    @Mock
    private ISubjectListService subjectListService;
    @Mock
    private ISubjectListByTeacherService subjectListByTeacherService;
    @Mock
    private ISubjectListPaginatedService subjectListPaginatedService;

    @BeforeEach
    void setUp() {
        SubjectGetController getController = new SubjectGetController(subjectGetService);
        SubjectListController listController = new SubjectListController(subjectListService, subjectListByTeacherService);
        SubjectListPaginatedController paginatedController = new SubjectListPaginatedController(subjectListPaginatedService);

        mockMvc = MockMvcBuilders.standaloneSetup(
            getController,
            listController,
            paginatedController
        ).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Nested
    @DisplayName("GET /admin/subjects/{id}")
    class GetSubject {

        @Test
        @DisplayName("When subject exists Then returns 200 with payload")
        void getSubject_success() throws Exception {
            SubjectResponseDto responseDto = SubjectResponseDto.builder().id(1L).name("Robotica").build();
            when(subjectGetService.cu33GetSubjectById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/admin/subjects/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
        }
    }

    @Nested
    @DisplayName("GET /admin/subjects")
    class ListSubjects {

        @Test
        @DisplayName("When listing all subjects Then returns 200 with list")
        void listSubjects_success() throws Exception {
            List<SubjectResponseDto> responses = List.of(
                SubjectResponseDto.builder().id(1L).name("Robotica").build(),
                SubjectResponseDto.builder().id(2L).name("IA").build()
            );
            when(subjectListService.cu31ListSubjects()).thenReturn(responses);

            mockMvc.perform(get("/admin/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
        }

        @Test
        @DisplayName("When listing subjects by teacher Then returns 200 with filtered list")
        void listSubjectsByTeacher_success() throws Exception {
            List<SubjectResponseDto> responses = List.of(
                SubjectResponseDto.builder().id(1L).name("Robotica").build()
            );
            when(subjectListByTeacherService.cu57ListSubjectsByTeacher(any(User.class))).thenReturn(responses);

            mockMvc.perform(get("/admin/subjects/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
        }
    }

    @Nested
    @DisplayName("GET /admin/subjects/paginated")
    class ListSubjectsPaginated {

        @Test
        @DisplayName("When paginating Then returns paginated response")
        void listSubjectsPaginated_success() throws Exception {
            PaginatedData<SubjectResponseDto> paginatedData = PaginatedData.<SubjectResponseDto>builder()
                .results(List.of(SubjectResponseDto.builder().id(1L).name("Robotica").build()))
                .count(40)
                .totalPages(4)
                .currentPage(2)
                .pageSize(10)
                .build();
            when(subjectListPaginatedService.cu32ListSubjectsPaginated(2, 10, "name", "desc", null,
                List.of("teacherId"), List.of("5"))).thenReturn(paginatedData);

            mockMvc.perform(get("/admin/subjects/paginated")
                    .param("page", "2")
                    .param("page_size", "10")
                    .param("order_by", "name")
                    .param("order_type", "desc")
                    .param("filters", "teacherId")
                    .param("filtersValues", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results[0].id").value(1L))
                .andExpect(jsonPath("$.data.count").value(40));
        }
    }
}
