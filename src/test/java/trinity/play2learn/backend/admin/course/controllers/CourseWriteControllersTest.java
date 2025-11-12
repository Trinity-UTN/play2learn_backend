package trinity.play2learn.backend.admin.course.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.dtos.CourseUpdateDto;
import trinity.play2learn.backend.admin.course.services.CourseRegisterService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseDeleteService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseUpdateService;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.configs.ArgumentSolvers.ArgumentSolversConfig;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.interceptors.JwtSessionAspect;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.services.jwt.JwtService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserGetByEmailService;

@WebMvcTest(
    controllers = {
        CourseRegisterController.class,
        CourseUpdateController.class,
        CourseDeleteController.class
    },
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtSessionAspect.class,
        ArgumentSolversConfig.class
    })
)
@DisplayName("CourseWriteControllers")
@AutoConfigureMockMvc(addFilters = false)
@Import(CourseWriteControllersTest.CourseWriteControllersTestConfig.class)
class CourseWriteControllersTest {

    private static final String BASE_URL = "/admin/courses";
    private static final Long COURSE_ID = 15L;
    private static final String COURSE_NAME = "Matemática";
    private static final Long YEAR_ID = 2025L;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private CourseRegisterService courseRegisterService;

    @Resource
    private ICourseUpdateService courseUpdateService;

    @Resource
    private ICourseDeleteService courseDeleteService;

    @Resource
    private JwtService jwtService;

    @Resource
    private IUserGetByEmailService userGetByEmailService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(courseRegisterService, courseUpdateService, courseDeleteService, jwtService, userGetByEmailService);
    }

    @Test
    @DisplayName("POST /admin/courses responde 201 con curso creado cuando los datos son válidos")
    void registerShouldCreateCourseSuccessfully() throws Exception {
        CourseRequestDto request = buildCourseRequest();
        CourseResponseDto responseDto = buildCourseResponse();
        Mockito.when(courseRegisterService.cu6RegisterCourse(Mockito.any())).thenReturn(responseDto);

        var mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Curso")))
            .andExpect(jsonPath("$.data.id").value(COURSE_ID))
            .andExpect(jsonPath("$.data.name").value(COURSE_NAME))
            .andReturn();

        assertThat(mvcResult.getResponse().getContentAsByteArray()).isNotEmpty();
        verify(courseRegisterService).cu6RegisterCourse(Mockito.any(CourseRequestDto.class));
    }

    @Test
    @DisplayName("POST /admin/courses responde 409 cuando el curso ya existe para el año")
    void registerShouldReturnConflictWhenCourseAlreadyExists() throws Exception {
        Mockito.doThrow(new ConflictException("Curso ya existe"))
            .when(courseRegisterService)
            .cu6RegisterCourse(Mockito.any());

        mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildCourseRequest()))
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /admin/courses/{id} responde 200 con curso actualizado")
    void updateShouldReturnOkWhenPayloadIsValid() throws Exception {
        CourseUpdateDto request = buildUpdateDto();
        Mockito.when(courseUpdateService.cu14UpdateCourse(Mockito.eq(COURSE_ID), Mockito.any()))
            .thenReturn(buildCourseResponse());

        mockMvc.perform(
                MockMvcRequestBuilders.put(BASE_URL + "/{id}", COURSE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(SuccessfulMessages.updatedSuccessfully("Curso")))
            .andExpect(jsonPath("$.data.id").value(COURSE_ID))
            .andExpect(jsonPath("$.data.name").value(COURSE_NAME));

        verify(courseUpdateService).cu14UpdateCourse(Mockito.eq(COURSE_ID), Mockito.any(CourseUpdateDto.class));
    }

    @Test
    @DisplayName("PUT /admin/courses/{id} responde 404 cuando el curso no existe")
    void updateShouldReturnNotFoundWhenCourseDoesNotExist() throws Exception {
        Mockito.doThrow(new NotFoundException("Curso no encontrado"))
            .when(courseUpdateService)
            .cu14UpdateCourse(Mockito.eq(COURSE_ID), Mockito.any());

        mockMvc.perform(
                MockMvcRequestBuilders.put(BASE_URL + "/{id}", COURSE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildUpdateDto()))
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /admin/courses/{id} responde 409 cuando el nombre ya existe en el año")
    void updateShouldReturnConflictWhenNameAlreadyExists() throws Exception {
        Mockito.doThrow(new ConflictException("Curso ya existe"))
            .when(courseUpdateService)
            .cu14UpdateCourse(Mockito.eq(COURSE_ID), Mockito.any());

        mockMvc.perform(
                MockMvcRequestBuilders.put(BASE_URL + "/{id}", COURSE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildUpdateDto()))
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /admin/courses/{id} responde 204 cuando elimina correctamente")
    void deleteShouldReturnNoContentWhenCourseDeleted() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/{id}", COURSE_ID)
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.message").value(SuccessfulMessages.deletedSuccessfully("Curso")));

        verify(courseDeleteService).cu15DeleteCourse(COURSE_ID);
    }

    @Test
    @DisplayName("DELETE /admin/courses/{id} responde 404 cuando el curso no existe")
    void deleteShouldReturnNotFoundWhenCourseMissing() throws Exception {
        Mockito.doThrow(new NotFoundException("Curso no encontrado"))
            .when(courseDeleteService)
            .cu15DeleteCourse(COURSE_ID);

        mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/{id}", COURSE_ID)
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /admin/courses/{id} responde 409 cuando existen asociaciones")
    void deleteShouldReturnConflictWhenAssociationsExist() throws Exception {
        Mockito.doThrow(new ConflictException("No se puede eliminar el curso"))
            .when(courseDeleteService)
            .cu15DeleteCourse(COURSE_ID);

        mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/{id}", COURSE_ID)
                    .header("X-ROLES", ROLE_ADMIN)
            )
            .andExpect(status().isConflict());
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

    private CourseRequestDto buildCourseRequest() {
        return CourseRequestDto.builder()
            .name(COURSE_NAME)
            .year_id(YEAR_ID)
            .build();
    }

    private CourseUpdateDto buildUpdateDto() {
        return CourseUpdateDto.builder()
            .name(COURSE_NAME)
            .build();
    }

    @TestConfiguration
    static class CourseWriteControllersTestConfig {

        @Bean
        CourseRegisterService courseRegisterService() {
            return Mockito.mock(CourseRegisterService.class);
        }

        @Bean
        ICourseUpdateService courseUpdateService() {
            return Mockito.mock(ICourseUpdateService.class);
        }

        @Bean
        ICourseDeleteService courseDeleteService() {
            return Mockito.mock(ICourseDeleteService.class);
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

