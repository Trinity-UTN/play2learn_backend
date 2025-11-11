package trinity.play2learn.backend.admin.year.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;

@DisplayName("YearMapper")
class YearMapperTest {

    private static final String NAME = "Primero Básico";

    @Nested
    @DisplayName("toModel")
    class ToModel {

        @Test
        @DisplayName("Crea un Year con el nombre del DTO y sin id")
        void createsModelWithDtoName() {
            YearRequestDto request = YearRequestDto.builder()
                .name(NAME)
                .build();

            Year result = YearMapper.toModel(request);

            assertThat(result.getName()).isEqualTo(NAME);
            assertThat(result.getId()).isNull();
            assertThat(result.getDeletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("Transforma la entidad en YearResponseDto copiando id y nombre")
        void mapsModelToDto() {
            Year year = buildYear(7L, "Séptimo Básico");

            YearResponseDto dto = YearMapper.toDto(year);

            assertThat(dto.getId()).isEqualTo(7L);
            assertThat(dto.getName()).isEqualTo("Séptimo Básico");
        }
    }

    @Nested
    @DisplayName("toListDto")
    class ToListDto {

        @Test
        @DisplayName("Convierte un Iterable de Year en lista de YearResponseDto manteniendo el orden")
        void convertsIterableToDtoList() {
            List<Year> years = List.of(
                buildYear(1L, NAME),
                buildYear(2L, "Segundo Básico")
            );

            List<YearResponseDto> dtos = YearMapper.toListDto(years);

            assertThat(dtos)
                .hasSize(2)
                .extracting(YearResponseDto::getId, YearResponseDto::getName)
                .containsExactly(
                    tuple(1L, NAME),
                    tuple(2L, "Segundo Básico")
                );
        }
    }

    private Year buildYear(Long id, String name) {
        return Year.builder()
            .id(id)
            .name(name)
            .build();
    }
}

