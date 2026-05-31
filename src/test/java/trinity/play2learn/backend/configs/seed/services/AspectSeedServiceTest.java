package trinity.play2learn.backend.configs.seed.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AspectSeedServiceTest {

    @Test
    @DisplayName("filenameToAspectName converts camelCase filename to readable name")
    void filenameToAspectName_convertsCamelCase() {
        assertThat(AspectSeedService.filenameToAspectName("CuerpoMasculinoRubioSinFondo.png"))
            .isEqualTo("Cuerpo Masculino Rubio Sin Fondo");
        assertThat(AspectSeedService.filenameToAspectName("White Hat.png"))
            .isEqualTo("White Hat");
    }
}
