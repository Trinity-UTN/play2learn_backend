package trinity.play2learn.backend.admin.course.controllers;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.services.CourseListService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseListPaginatedService;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.configs.ArgumentSolvers.ArgumentSolversConfig;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.interceptors.JwtSessionAspect;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.services.jwt.JwtService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserGetByEmailService;

@WebMvcTest(
    controllers = {
        CourseGetController.class,
        CourseListController.class,
        CourseListPaginatedController.class
    },
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtSessionAspect.class,
        ArgumentSolversConfig.class
    })
)
@DisplayName("CourseReadControllers")
@AutoConfigureMockMvc(addFilters = false)
@Import(CourseReadControllersTest.CourseReadControllersTestConfig.class)
class CourseReadControllersTest {

    private static final String BASE_URL = "/admin/courses";
    private static final Long COURSE_ID = 21L;
    private static final String COURSE_NAME = "Matemática";
    private static final Long YEAR_ID = 2025L;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ICourseGetService courseGetService;

    @Resource
    private CourseListService courseListService;

    @Resource
    private ICourseListPaginatedService courseListPaginatedService;

    @Resource
    private JwtService jwtService;

    @Resource
    private IUserGetByEmailService userGetByEmailService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(courseGetService, courseListService, courseListPaginatedService, jwtService, userGetByEmailService);
    }

    @Test
    @DisplayName("GET /admin/courses/{id} responde 200 con curso encontrado")
    void getShouldReturnCourse() throws Exception {
        Mockito.when(courseGetService.cu17GetCourse(Mockito.eq(COURSE_ID))).thenReturn(buildCourseResponse());

        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "/{id}", COURSE_ID)
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()))
            .andExpect(jsonPath("$.data.id").value(COURSE_ID))
            .andExpect(jsonPath("$.data.name").value(COURSE_NAME));

        verify(courseGetService).cu17GetCourse(Mockito.eq(COURSE_ID));
    }

    @Test
    @DisplayName("GET /admin/courses/{id} responde 404 cuando el curso no existe")
    void getShouldReturnNotFoundWhenCourseMissing() throws Exception {
        Mockito.doThrow(new NotFoundException("Curso no encontrado"))
            .when(courseGetService)
            .cu17GetCourse(Mockito.eq(COURSE_ID));

        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "/{id}", COURSE_ID)
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /admin/courses responde 200 con el listado activo")
    void listShouldReturnCourses() throws Exception {
        List<CourseResponseDto> courses = List.of(
            buildCourseResponse(),
            CourseResponseDto.builder()
                .id(22L)
                .name("Lengua")
                .year(YearResponseDto.builder()
                    .id(YEAR_ID + 1)
                    .name("2025")
                    .build())
                .build()
        );
        Mockito.when(courseListService.cu9ListCourses()).thenReturn(courses);

        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL)
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].name").value(COURSE_NAME))
            .andExpect(jsonPath("$.data[1].name").value("Lengua"));

        verify(courseListService).cu9ListCourses();
    }

    @Test
    @DisplayName("GET /admin/courses/paginated responde 200 con parámetros por defecto")
    void listPaginatedShouldReturnDefaultPage() throws Exception {
        PaginatedData<CourseResponseDto> paginated = buildPaginatedData(List.of(buildCourseResponse()), 1, 10, 1, 1);
        Mockito.when(courseListPaginatedService.cu16ListPaginatedCourses(
            Mockito.eq(1), Mockito.eq(10), Mockito.eq("id"), Mockito.eq("asc"),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull()
        )).thenReturn(paginated);

        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "/paginated")
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()))
            .andExpect(jsonPath("$.data.results[0].name").value(COURSE_NAME))
            .andExpect(jsonPath("$.data.count").value(1))
            .andExpect(jsonPath("$.data.pageSize").value(10));

        verify(courseListPaginatedService).cu16ListPaginatedCourses(
            Mockito.eq(1), Mockito.eq(10), Mockito.eq("id"), Mockito.eq("asc"),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
    }

    @Test
    @DisplayName("GET /admin/courses/paginated responde 200 aplicando filtros y búsqueda")
    void listPaginatedShouldApplyFilters() throws Exception {
        PaginatedData<CourseResponseDto> paginated = buildPaginatedData(List.of(), 2, 5, 3, 0);
        Mockito.when(courseListPaginatedService.cu16ListPaginatedCourses(
            Mockito.eq(2), Mockito.eq(5), Mockito.eq("name"), Mockito.eq("desc"),
            Mockito.eq("mat"), Mockito.eq(List.of("year")), Mockito.eq(List.of("2025"))
        )).thenReturn(paginated);

        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "/paginated")
                    .param("page", "2")
                    .param("page_size", "5")
                    .param("order_by", "name")
                    .param("order_type", "desc")
                    .param("search", "mat")
                    .param("filters", "year")
                    .param("filtersValues", "2025")
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.currentPage").value(2))
            .andExpect(jsonPath("$.data.pageSize").value(5))
            .andExpect(jsonPath("$.data.totalPages").value(3))
            .andExpect(jsonPath("$.data.results").isArray());

        verify(courseListPaginatedService).cu16ListPaginatedCourses(
            Mockito.eq(2), Mockito.eq(5), Mockito.eq("name"), Mockito.eq("desc"),
            Mockito.eq("mat"), Mockito.eq(List.of("year")), Mockito.eq(List.of("2025")));
    }

    private CourseResponseDto buildCourseResponse() {
        return CourseResponseDto.builder()
            .id(COURSE_ID)
            .name(COURSE_NAME)
            .year(YearResponseDto.builder()
                .id(YEAR_ID)
                .name("2025")
                .build())
            .build();
    }

    private PaginatedData<CourseResponseDto> buildPaginatedData(
        List<CourseResponseDto> results,
        int currentPage,
        int pageSize,
        int totalPages,
        int count
    ) {
        return PaginatedData.<CourseResponseDto>builder()
            .results(results)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .totalPages(totalPages)
            .count(count)
            .build();
    }

    @TestConfiguration
    static class CourseReadControllersTestConfig {

        @Bean
        ICourseGetService courseGetService() {
            return Mockito.mock(ICourseGetService.class);
        }

        @Bean
        CourseListService courseListService() {
            return Mockito.mock(CourseListService.class);
        }

        @Bean
        ICourseListPaginatedService courseListPaginatedService() {
            return Mockito.mock(ICourseListPaginatedService.class);
        }

        @Bean
        JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        IUserGetByEmailService userGetByEmailService() {
            return Mockito.mock(IUserGetByEmailService.class);
        }
    }
}

