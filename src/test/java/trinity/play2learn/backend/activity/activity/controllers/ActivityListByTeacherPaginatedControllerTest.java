package trinity.play2learn.backend.activity.activity.controllers;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherSimpleDto;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListByTeacherPaginatedService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityListByTeacherPaginatedControllerTest {

    private static final String BASE_PATH = "/activity/teacher/paginated";
    private static final int PAGE = 1;
    private static final int PAGE_SIZE = 10;

    private MockMvc mockMvc;

    @Mock
    private IActivityListByTeacherPaginatedService activityListByTeacherPaginatedService;

    private PaginatedData<ActivityTeacherSimpleDto> paginatedResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ActivityListByTeacherPaginatedController(activityListByTeacherPaginatedService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        paginatedResponse = buildPaginatedResponse();

        reset(activityListByTeacherPaginatedService);
    }

    private PaginatedData<ActivityTeacherSimpleDto> buildPaginatedResponse() {
        List<ActivityTeacherSimpleDto> content = buildTeacherSimpleDtos();
        return PaginatedData.<ActivityTeacherSimpleDto>builder()
            .results(content)
            .count(2)
            .currentPage(PAGE)
            .totalPages(1)
            .pageSize(PAGE_SIZE)
            .build();
    }

    private List<ActivityTeacherSimpleDto> buildTeacherSimpleDtos() {
        return List.of(
            ActivityTeacherSimpleDto.builder()
                .id(1L)
                .name("Ahorcado")
                .status(ActivityStatus.PUBLISHED)
                .build(),
            ActivityTeacherSimpleDto.builder()
                .id(2L)
                .name("Preguntados")
                .status(ActivityStatus.PUBLISHED)
                .build()
        );
    }

    private PaginatedData<ActivityTeacherSimpleDto> buildEmptyPaginatedResponse() {
        return PaginatedData.<ActivityTeacherSimpleDto>builder()
            .results(List.of())
            .count(0)
            .currentPage(PAGE)
            .totalPages(0)
            .pageSize(PAGE_SIZE)
            .build();
    }

    @Nested
    @DisplayName("GET /activity/teacher/paginated")
    class ListByTeacherPaginated {

        @Test
        @DisplayName("Given teacher with activities When listing paginated Then returns paginated data with 200 OK")
        void shouldReturnActivitiesPaginated() throws Exception {
            // Given
            when(activityListByTeacherPaginatedService.cu111ListActivityByTeacherPaginated(
                org.mockito.ArgumentMatchers.eq(PAGE),
                org.mockito.ArgumentMatchers.eq(PAGE_SIZE),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(paginatedResponse);

            // When & Then
            performGet()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(PAGE))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(PAGE_SIZE))
                .andExpect(jsonPath("$.data.results.length()").value(2))
                .andExpect(jsonPath("$.data.results[0].id").value(1L))
                .andExpect(jsonPath("$.data.results[0].name").value("Ahorcado"))
                .andExpect(jsonPath("$.data.results[1].id").value(2L))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityListByTeacherPaginatedService).cu111ListActivityByTeacherPaginated(
                org.mockito.ArgumentMatchers.eq(PAGE),
                org.mockito.ArgumentMatchers.eq(PAGE_SIZE),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher with no activities When listing paginated Then returns empty paginated data with 200 OK")
        void shouldReturnEmptyPaginatedData() throws Exception {
            // Given
            PaginatedData<ActivityTeacherSimpleDto> emptyPaginated = buildEmptyPaginatedResponse();

            when(activityListByTeacherPaginatedService.cu111ListActivityByTeacherPaginated(
                org.mockito.ArgumentMatchers.eq(PAGE),
                org.mockito.ArgumentMatchers.eq(PAGE_SIZE),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(emptyPaginated);

            // When & Then
            performGet()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.data.results.length()").value(0))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityListByTeacherPaginatedService).cu111ListActivityByTeacherPaginated(
                org.mockito.ArgumentMatchers.eq(PAGE),
                org.mockito.ArgumentMatchers.eq(PAGE_SIZE),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performGet() throws Exception {
        return mockMvc.perform(get(BASE_PATH)
            .param("page", String.valueOf(PAGE))
            .param("page_size", String.valueOf(PAGE_SIZE)));
    }
}

