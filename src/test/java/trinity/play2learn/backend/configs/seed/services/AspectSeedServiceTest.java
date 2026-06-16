package trinity.play2learn.backend.configs.seed.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.configs.seed.services.AspectSeedService.AspectCatalogRow;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;

class AspectSeedServiceTest {

    @Test
    @DisplayName("parseCatalogLine lee fila CSV con comillas y precio")
    void parseCatalogLine_readsQuotedCsvRow() {
        AspectCatalogRow row = AspectSeedService.parseCatalogLine(
            "CUERPO;\"Cuerpo Masculino 1\";\"https://i.ibb.co/WNBgmk74/78ec3de94a34.png\";10");

        assertThat(row.type()).isEqualTo(TypeAspect.CUERPO);
        assertThat(row.name()).isEqualTo("Cuerpo Masculino 1");
        assertThat(row.imageUrl()).isEqualTo("https://i.ibb.co/WNBgmk74/78ec3de94a34.png");
        assertThat(row.price()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("parseCatalogLine rechaza filas con columnas incorrectas")
    void parseCatalogLine_rejectsInvalidColumnCount() {
        assertThatThrownBy(() -> AspectSeedService.parseCatalogLine("CUERPO;\"Solo dos\""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("se esperaban 4 columnas");
    }

    @Test
    @DisplayName("parseCatalogLine rechaza tipos de aspecto desconocidos")
    void parseCatalogLine_rejectsUnknownType() {
        assertThatThrownBy(() ->
            AspectSeedService.parseCatalogLine("INVALIDO;\"Nombre\";\"https://example.com/img.png\";10"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("tipo de aspecto inválido");
    }
}
