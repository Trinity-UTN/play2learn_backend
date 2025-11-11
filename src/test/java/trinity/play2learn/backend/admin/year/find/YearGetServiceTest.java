package trinity.play2learn.backend.admin.year.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.services.YearGetService;
import trinity.play2learn.backend.admin.year.services.commons.YearGetByIdService;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearGetService")
class YearGetServiceTest {

    @Mock
    private YearGetByIdService yearGetByIdService;

    @InjectMocks
    private YearGetService yearGetService;

    @Nested
    @DisplayName("cu13GetYear")
    class Cu13GetYear {

        @Test
        @DisplayName("Obtiene un año por ID y lo transforma a DTO")
        void shouldReturnYearDtoById() {
            Year year = buildYear(5L, "Quinto Básico");

            when(yearGetByIdService.findById(5L)).thenReturn(year);

            YearResponseDto response = yearGetService.cu13GetYear(5L);

            verify(yearGetByIdService).findById(5L);
            assertThat(response.getId()).isEqualTo(5L);
            assertThat(response.getName()).isEqualTo("Quinto Básico");
        }
    }

    private Year buildYear(Long id, String name) {
        return Year.builder()
            .id(id)
            .name(name)
            .build();
    }
}

