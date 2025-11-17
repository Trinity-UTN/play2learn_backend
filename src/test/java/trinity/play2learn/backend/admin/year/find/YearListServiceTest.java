package trinity.play2learn.backend.admin.year.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.YearListService;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearListService")
class YearListServiceTest {

    @Mock
    private IYearRepository yearRepository;

    @InjectMocks
    private YearListService yearListService;

    @Nested
    @DisplayName("cu8ListYears")
    class Cu8ListYears {

        @Test
        @DisplayName("Retorna la lista de años activos transformada a DTO")
        void shouldReturnActiveYearsAsDtoList() {
            List<Year> activeYears = List.of(
                buildYear(1L, "Primero Básico"),
                buildYear(2L, "Segundo Básico")
            );

            when(yearRepository.findAllByDeletedAtIsNull()).thenReturn(activeYears);

            List<YearResponseDto> result = yearListService.cu8ListYears();

            verify(yearRepository).findAllByDeletedAtIsNull();
            assertThat(result)
                .hasSize(2)
                .extracting(YearResponseDto::getName)
                .containsExactly("Primero Básico", "Segundo Básico");
        }
    }

    private Year buildYear(Long id, String name) {
        return Year.builder()
            .id(id)
            .name(name)
            .build();
    }
}

