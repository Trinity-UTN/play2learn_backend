package trinity.play2learn.backend.admin.teacher.controllers;

import static org.mockito.Mockito.reset;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherListPaginatedService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherListService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;

@ExtendWith(MockitoExtension.class)
class TeacherReadControllersTest {

    private static final String BASE_PATH = "/admin/teachers";
    private static final String PAGINATED_PATH = "/admin/teachers/paginated";
    private static final Long TEACHER_ID = 10L;

    private MockMvc mockMvc;

    @Mock
    private ITeacherGetService teacherGetService;

    @Mock
    private ITeacherListService teacherListService;

    @Mock
    private ITeacherListPaginatedService teacherListPaginatedService;

    private TeacherResponseDto teacherResponse;
    private List<TeacherResponseDto> teacherResponses;
    private PaginatedData<TeacherResponseDto> paginatedResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new TeacherGetController(teacherGetService),
                new TeacherListController(teacherListService),
                new TeacherListPaginatedController(teacherListPaginatedService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        teacherResponse = toDto(TeacherTestMother.teacher(TEACHER_ID, "12345678"));
        teacherResponses = List.of(teacherResponse, toDto(TeacherTestMother.teacher(11L, "87654321")));

        Page<TeacherResponseDto> page = new PageImpl<>(
            teacherResponses,
            PageRequest.of(0, teacherResponses.size()),
            teacherResponses.size()
        );

        paginatedResponse = PaginatedData.<TeacherResponseDto>builder()
            .results(page.getContent())
            .count((int) page.getTotalElements())
            .currentPage(page.getNumber() + 1)
            .totalPages(page.getTotalPages())
            .pageSize(page.getSize())
            .build();

        reset(teacherGetService, teacherListService, teacherListPaginatedService);
    }

    @Nested
    @DisplayName("GET /admin/teachers/{id}")
    class GetById {

        @Test
        @DisplayName("Should return teacher details with 200 OK")
        void shouldReturnTeacherById() throws Exception {
            when(teacherGetService.cu28GetTeacherById(TEACHER_ID)).thenReturn(teacherResponse);

            performGetById(TEACHER_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(teacherResponse.getId()))
                .andExpect(jsonPath("$.data.dni").value(teacherResponse.getDni()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(teacherGetService).cu28GetTeacherById(TEACHER_ID);
        }

        @Test
        @DisplayName("Should return 404 when teacher is not found")
        void shouldReturnNotFound() throws Exception {
            when(teacherGetService.cu28GetTeacherById(TEACHER_ID))
                .thenThrow(new NotFoundException("Docente no encontrado"));

            performGetById(TEACHER_ID)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Docente no encontrado"))
                .andExpect(jsonPath("$.errors[0]").value("Docente no encontrado"));
        }
    }

    @Nested
    @DisplayName("GET /admin/teachers")
    class ListAll {

        @Test
        @DisplayName("Should return list of teachers with 200 OK")
        void shouldReturnTeacherList() throws Exception {
            when(teacherListService.cu25ListTeachers()).thenReturn(teacherResponses);

            performGetAll()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(teacherResponses.size()))
                .andExpect(jsonPath("$.data[0].id").value(teacherResponses.get(0).getId()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(teacherListService).cu25ListTeachers();
        }

        @Test
        @DisplayName("Should return empty list with 200 OK when no teachers exist")
        void shouldReturnEmptyList() throws Exception {
            when(teacherListService.cu25ListTeachers()).thenReturn(List.of());

            performGetAll()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(teacherListService).cu25ListTeachers();
        }
    }

    @Nested
    @DisplayName("GET /admin/teachers/paginated")
    class PaginatedList {

        @Test
        @DisplayName("Should return paginated data with 200 OK")
        void shouldReturnPaginatedTeachers() throws Exception {
            when(teacherListPaginatedService.cu26ListTeachersPaginated(
                1, 2, "id", "asc", null, null, null
            )).thenReturn(paginatedResponse);

            performGetPaginated(1, 2, "id", "asc", null, null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results.length()").value(teacherResponses.size()))
                .andExpect(jsonPath("$.data.count").value(teacherResponses.size()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(teacherListPaginatedService).cu26ListTeachersPaginated(1, 2, "id", "asc", null, null, null);
        }

        @Test
        @DisplayName("Should return paginated data with 200 OK when no teachers exist")
        void shouldReturnEmptyPaginatedList() throws Exception {
            when(teacherListPaginatedService.cu26ListTeachersPaginated(
                1, 2, "id", "asc", null, null, null
            )).thenReturn(PaginatedData.<TeacherResponseDto>builder().results(List.of()).count(0).currentPage(1).totalPages(0).pageSize(2).build());

            performGetPaginated(1, 2, "id", "asc", null, null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results.length()").value(0))
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(teacherListPaginatedService).cu26ListTeachersPaginated(1, 2, "id", "asc", null, null, null);
        }
    }

    private TeacherResponseDto toDto(Teacher teacher) {
        return TeacherResponseDto.builder()
            .id(teacher.getId())
            .name(teacher.getName())
            .lastname(teacher.getLastname())
            .dni(teacher.getDni())
            .user(UserResponseDto.builder()
                .id(teacher.getUser().getId())
                .email(teacher.getUser().getEmail())
                .build())
            .active(true)
            .build();
    }

    private ResultActions performGetById(long id) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/{id}", id));
    }

    private ResultActions performGetAll() throws Exception {
        return mockMvc.perform(get(BASE_PATH));
    }

    private ResultActions performGetPaginated(int page, int pageSize, String orderBy, String orderType, String search, List<String> filters, List<String> filterValues) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(PAGINATED_PATH)
            .param("page", String.valueOf(page))
            .param("page_size", String.valueOf(pageSize))
            .param("order_by", orderBy)
            .param("order_type", orderType);

        if (search != null && !search.isEmpty()) {
            requestBuilder.param("search", search);
        }
        if (filters != null) {
            filters.forEach(filter -> requestBuilder.param("filters", filter));
        }
        if (filterValues != null) {
            filterValues.forEach(value -> requestBuilder.param("filtersValues", value));
        }

        return mockMvc.perform(requestBuilder);
    }
}

