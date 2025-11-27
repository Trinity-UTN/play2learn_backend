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

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityApprovedListPaginatedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNotApprovedListPaginatedService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityListPaginatedByStudentControllerTest {

    private static final String BASE_PATH = "/activity/student/paginated";
    private static final String NOT_APPROVED_PATH = BASE_PATH + "/not-approved";
    private static final String APPROVED_PATH = BASE_PATH + "/approved";
    private static final int PAGE = 1;
    private static final int PAGE_SIZE = 10;

    private MockMvc mockMvc;

    @Mock
    private IActivityNotApprovedListPaginatedService activityNotApprovedListPaginatedService;

    @Mock
    private IActivityApprovedListPaginatedService activityApprovedListPaginatedService;

    private PaginatedData<ActivityStudentNotApprovedResponseDto> notApprovedPaginated;
    private PaginatedData<ActivityStudentApprovedResponseDto> approvedPaginated;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ActivityListPaginatedByStudentController(
                    activityNotApprovedListPaginatedService,
                    activityApprovedListPaginatedService
                )
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        notApprovedPaginated = buildNotApprovedPaginated();
        approvedPaginated = buildApprovedPaginated();

        reset(activityNotApprovedListPaginatedService, activityApprovedListPaginatedService);
    }

    private PaginatedData<ActivityStudentNotApprovedResponseDto> buildNotApprovedPaginated() {
        List<ActivityStudentNotApprovedResponseDto> content = List.of(
            ActivityStudentNotApprovedResponseDto.builder()
                .id(1L)
                .name("Ahorcado")
                .remainingAttempts(2)
                .build()
        );
        return buildPaginatedData(content, 1);
    }

    private PaginatedData<ActivityStudentApprovedResponseDto> buildApprovedPaginated() {
        List<ActivityStudentApprovedResponseDto> content = List.of(
            ActivityStudentApprovedResponseDto.builder()
                .id(2L)
                .name("Preguntados")
                .reward(50.0)
                .build()
        );
        return buildPaginatedData(content, 1);
    }

    private <T> PaginatedData<T> buildPaginatedData(List<T> content, int count) {
        return PaginatedData.<T>builder()
            .results(content)
            .count(count)
            .currentPage(PAGE)
            .totalPages(1)
            .pageSize(PAGE_SIZE)
            .build();
    }

    @Nested
    @DisplayName("GET /activity/student/paginated/not-approved")
    class ListNotApprovedPaginated {

        @Test
        @DisplayName("Given student with not approved activities When listing paginated Then returns paginated data with 200 OK")
        void shouldReturnNotApprovedActivitiesPaginated() throws Exception {
            // Given
            when(activityNotApprovedListPaginatedService.cu66listNotApprovedActivitiesPaginated(
                org.mockito.ArgumentMatchers.eq(PAGE),
                org.mockito.ArgumentMatchers.eq(PAGE_SIZE),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(notApprovedPaginated);

            // When & Then
            performGetNotApprovedPaginated()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(PAGE))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(PAGE_SIZE))
                .andExpect(jsonPath("$.data.results.length()").value(1))
                .andExpect(jsonPath("$.data.results[0].id").value(1L))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityNotApprovedListPaginatedService).cu66listNotApprovedActivitiesPaginated(
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

    @Nested
    @DisplayName("GET /activity/student/paginated/approved")
    class ListApprovedPaginated {

        @Test
        @DisplayName("Given student with approved activities When listing paginated Then returns paginated data with 200 OK")
        void shouldReturnApprovedActivitiesPaginated() throws Exception {
            // Given
            when(activityApprovedListPaginatedService.cu69ListApprovedActivitiesPaginated(
                org.mockito.ArgumentMatchers.eq(PAGE),
                org.mockito.ArgumentMatchers.eq(PAGE_SIZE),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(approvedPaginated);

            // When & Then
            performGetApprovedPaginated()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(PAGE))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(PAGE_SIZE))
                .andExpect(jsonPath("$.data.results.length()").value(1))
                .andExpect(jsonPath("$.data.results[0].id").value(2L))
                .andExpect(jsonPath("$.data.results[0].reward").value(50.0))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityApprovedListPaginatedService).cu69ListApprovedActivitiesPaginated(
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

    private ResultActions performGetNotApprovedPaginated() throws Exception {
        return mockMvc.perform(get(NOT_APPROVED_PATH)
            .param("page", String.valueOf(PAGE))
            .param("page_size", String.valueOf(PAGE_SIZE)));
    }

    private ResultActions performGetApprovedPaginated() throws Exception {
        return mockMvc.perform(get(APPROVED_PATH)
            .param("page", String.valueOf(PAGE))
            .param("page_size", String.valueOf(PAGE_SIZE)));
    }
}

