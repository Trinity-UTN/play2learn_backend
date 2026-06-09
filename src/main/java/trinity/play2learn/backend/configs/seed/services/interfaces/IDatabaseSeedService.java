package trinity.play2learn.backend.configs.seed.services.interfaces;

import trinity.play2learn.backend.configs.seed.dtos.SeedResultDto;

public interface IDatabaseSeedService {

    /**
     * Ejecuta el repoblado completo de la base de datos en el orden definido por las fases de seed.
     *
     * @return resumen con credenciales, conteos y ruta del archivo markdown generado
     */
    SeedResultDto execute();
}
