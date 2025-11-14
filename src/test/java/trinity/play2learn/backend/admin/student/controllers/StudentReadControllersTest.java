package trinity.play2learn.backend.admin.student.controllers;

import static org.mockito.Mockito.verify;
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

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentListPaginatedService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentListService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.response.PaginatedData;

@ExtendWith(MockitoExtension.class)
class StudentReadControllersTest {

    private static final String BASE_URL = "/admin/students";

    private MockMvc mockMvc;

    @Mock
    private IStudentGetService studentGetService;
    @Mock
    private IStudentListService studentListService;
    @Mock
    private IStudentListPaginatedService studentListPaginatedService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(
                new StudentGetController(studentGetService),
                new StudentListController(studentListService),
                new StudentListPaginatedController(studentListPaginatedService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Nested
    @DisplayName("GET /admin/students/{id}")
    class GetStudent {

        @Test
        @DisplayName("When student exists Then returns 200 with payload")
        void getStudent_success() throws Exception {
            StudentResponseDto responseDto = studentResponse(100L, "Juan");
            when(studentGetService.cu22GetStudent(100L)).thenReturn(responseDto);

            mockMvc.perform(get(BASE_URL + "/{id}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value(responseDto.getName()))
                .andExpect(jsonPath("$.message").exists());

            verify(studentGetService).cu22GetStudent(100L);
        }

        @Test
        @DisplayName("When student not found Then returns 404")
        void getStudent_notFound() throws Exception {
            when(studentGetService.cu22GetStudent(500L)).thenThrow(new NotFoundException("student"));

            mockMvc.perform(get(BASE_URL + "/{id}", 500L))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /admin/students")
    class ListStudents {

        @Test
        @DisplayName("When students exist Then returns 200 with list")
        void listStudents_success() throws Exception {
            StudentResponseDto first = studentResponse(1L, "Ana");
            StudentResponseDto second = studentResponse(2L, "Luis");
            when(studentListService.cu20ListStudents()).thenReturn(List.of(first, second));

            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(first.getId()))
                .andExpect(jsonPath("$.data[0].name").value(first.getName()))
                .andExpect(jsonPath("$.data[1].id").value(second.getId()))
                .andExpect(jsonPath("$.data[1].name").value(second.getName()));

            verify(studentListService).cu20ListStudents();
        }

        @Test
        @DisplayName("When there are no students Then returns empty list")
        void listStudents_empty() throws Exception {
            when(studentListService.cu20ListStudents()).thenReturn(List.of());

            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /admin/students/paginated")
    class ListPaginatedStudents {

        @Test
        @DisplayName("When paginated retrieval succeeds Then returns 200 with metadata")
        void listPaginated_success() throws Exception {
            StudentResponseDto studentDto = StudentResponseDto.builder()
                .id(10L)
                .name("Carla")
                .build();
            PaginatedData<StudentResponseDto> paginatedData = PaginatedData.<StudentResponseDto>builder()
                .results(List.of(studentDto))
                .count(1)
                .totalPages(1)
                .currentPage(1)
                .pageSize(10)
                .build();

            when(studentListPaginatedService.cu21ListPaginatedStudents(1, 10, "id", "asc", null, null, null))
                .thenReturn(paginatedData);

            mockMvc.perform(get(BASE_URL + "/paginated")
                    .param("page", "1")
                    .param("page_size", "10")
                    .param("order_by", "id")
                    .param("order_type", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results[0].id").value(10L))
                .andExpect(jsonPath("$.data.count").value(1));

            verify(studentListPaginatedService)
                .cu21ListPaginatedStudents(1, 10, "id", "asc", null, null, null);
        }

        @Test
        @DisplayName("When paginated retrieval returns empty Then returns 200 with empty results")
        void listPaginated_empty() throws Exception {
            PaginatedData<StudentResponseDto> emptyPage = PaginatedData.<StudentResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(10)
                .build();

            when(studentListPaginatedService.cu21ListPaginatedStudents(1, 10, "id", "asc", "search", List.of("filter"), List.of("value")))
                .thenReturn(emptyPage);

            mockMvc.perform(get(BASE_URL + "/paginated")
                    .param("page", "1")
                    .param("page_size", "10")
                    .param("order_by", "id")
                    .param("order_type", "asc")
                    .param("search", "search")
                    .param("filters", "filter")
                    .param("filtersValues", "value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isEmpty())
                .andExpect(jsonPath("$.data.count").value(0));
        }

        @Test
        @DisplayName("When underlying service throws NotFound Then returns 404")
        void listPaginated_notFound() throws Exception {
            when(studentListPaginatedService.cu21ListPaginatedStudents(2, 5, "name", "desc", null, null, null))
                .thenThrow(new NotFoundException("page"));

            mockMvc.perform(get(BASE_URL + "/paginated")
                    .param("page", "2")
                    .param("page_size", "5")
                    .param("order_by", "name")
                    .param("order_type", "desc"))
                .andExpect(status().isNotFound());
        }
    }

    private StudentResponseDto studentResponse(Long id, String name) {
        return StudentResponseDto.builder()
            .id(id)
            .name(name)
            .lastname("Test")
            .dni("12345678")
            .course(null)
            .build();
    }
}

