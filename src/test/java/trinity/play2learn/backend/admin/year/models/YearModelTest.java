package trinity.play2learn.backend.admin.year.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Year model")
class YearModelTest {

    private static final String NAME = "Primero Básico";

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Establece deletedAt con la marca de tiempo actual")
        void setsDeletedAtToNow() {
            Year year = buildYear(1L, NAME);

            LocalDateTime before = LocalDateTime.now();
            year.delete();
            LocalDateTime after = LocalDateTime.now();

            assertThat(year.getDeletedAt()).isNotNull();
            assertThat(year.getDeletedAt()).isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("restore")
    class Restore {

        @Test
        @DisplayName("Restablece deletedAt a null aun después de una eliminación lógica")
        void resetsDeletedAtToNull() {
            Year year = buildYear(2L, "Segundo Básico");

            year.delete();
            assertThat(year.getDeletedAt()).isNotNull();

            year.restore();

            assertThat(year.getDeletedAt()).isNull();
        }
    }

    private Year buildYear(Long id, String name) {
        return Year.builder()
            .id(id)
            .name(name)
            .build();
    }
}

