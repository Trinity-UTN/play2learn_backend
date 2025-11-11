package trinity.play2learn.backend.admin.year.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepositoryPaginated;
import trinity.play2learn.backend.admin.year.services.YearListPaginatedService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearListPaginatedService")
class YearListPaginatedServiceTest {

    @Mock
    private IYearRepositoryPaginated yearRepository;

    @InjectMocks
    private YearListPaginatedService yearListPaginatedService;

    @Nested
    @DisplayName("cu12PaginatedListYears")
    class Cu12PaginatedListYears {

        @Test
        @DisplayName("Retorna página sin filtros adicionales y mapea contenido a DTO")
        void returnsPaginatedYearsWithoutFilters() {
            Pageable pageable = Pageable.ofSize(10);
            Page<Year> page = new PageImpl<>(
                List.of(
                    buildYear(1L, "Primero Básico"),
                    buildYear(2L, "Segundo Básico")
                )
            );
            List<YearResponseDto> dtoList = List.of(
                buildDto(1L, "Primero Básico"),
                buildDto(2L, "Segundo Básico")
            );
            PaginatedData<YearResponseDto> expected = buildPaginatedData(dtoList, 1, 10);

            try (var mockedPaginator = Mockito.mockStatic(PaginatorUtils.class);
                 var mockedPaginationHelper = Mockito.mockStatic(PaginationHelper.class)) {
                mockedPaginator.when(() -> PaginatorUtils.buildPageable(1, 10, "id", "asc")).thenReturn(pageable);
                mockedPaginationHelper
                    .when(() -> PaginationHelper.fromPage(page, dtoList))
                    .thenReturn(expected);

                when(yearRepository.findAll(Mockito.<Specification<Year>>any(), any(Pageable.class))).thenReturn(page);

                PaginatedData<YearResponseDto> result = yearListPaginatedService.cu12PaginatedListYears(
                    1,
                    10,
                    "id",
                    "asc",
                    null,
                    null,
                    null
                );

                assertThat(result).isEqualTo(expected);
                verify(yearRepository).findAll(Mockito.<Specification<Year>>any(), any(Pageable.class));
            }
        }

        @Test
        @DisplayName("Aplica búsqueda textual y filtros dinámicos antes de consultar el repositorio")
        void appliesSearchAndFilters() {
            Pageable pageable = Pageable.ofSize(5);
            Page<Year> page = Page.empty(pageable);
            List<YearResponseDto> dtoList = List.of();
            PaginatedData<YearResponseDto> expected = PaginatedData.<YearResponseDto>builder()
                .results(dtoList)
                .count(0)
                .totalPages(0)
                .currentPage(2)
                .pageSize(5)
                .build();

            try (var mockedPaginator = Mockito.mockStatic(PaginatorUtils.class);
                 var mockedPaginationHelper = Mockito.mockStatic(PaginationHelper.class)) {
                mockedPaginator.when(() -> PaginatorUtils.buildPageable(2, 5, "name", "desc")).thenReturn(pageable);
                mockedPaginationHelper
                    .when(() -> PaginationHelper.fromPage(page, dtoList))
                    .thenReturn(expected);

                when(yearRepository.findAll(Mockito.<Specification<Year>>any(), any(Pageable.class))).thenReturn(page);

                PaginatedData<YearResponseDto> result = yearListPaginatedService.cu12PaginatedListYears(
                    2,
                    5,
                    "name",
                    "desc",
                    "Básico",
                    List.of("name"),
                    List.of("Primero Básico")
                );

                assertThat(result).isEqualTo(expected);
                verify(yearRepository).findAll(Mockito.<Specification<Year>>any(), any(Pageable.class));
            }
        }
    }

    private Year buildYear(Long id, String name) {
        return Year.builder()
            .id(id)
            .name(name)
            .build();
    }

    private YearResponseDto buildDto(Long id, String name) {
        return YearResponseDto.builder()
            .id(id)
            .name(name)
            .build();
    }

    private PaginatedData<YearResponseDto> buildPaginatedData(List<YearResponseDto> data, int currentPage, int pageSize) {
        return PaginatedData.<YearResponseDto>builder()
            .results(data)
            .count(data.size())
            .totalPages(1)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .build();
    }
}

