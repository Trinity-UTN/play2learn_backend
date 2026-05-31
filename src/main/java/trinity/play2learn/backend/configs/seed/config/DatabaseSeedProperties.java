package trinity.play2learn.backend.configs.seed.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuración del servicio de repoblado de base de datos (solo entornos de desarrollo).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.seed")
public class DatabaseSeedProperties {

    private boolean enabled = false;

    private int maxYears = 6;

    private int maxCoursesPerYear = 2;

    private int maxSubjectsPerCourse = 3;

    private int maxStudentsPerYear = 30;

    private int minTeachers = 6;

    private List<String> yearNames = List.of(
        "Primer Año",
        "Segundo Año",
        "Tercer Año",
        "Cuarto Año",
        "Quinto Año",
        "Sexto Año"
    );

    private List<String> courseNames = List.of("A", "B");

    private List<String> defaultSubjects = List.of("Matemática", "Lengua", "Geografía");

    private String devEmail = "dev@gmail.com";

    private String devPassword = "12345678";

    private String adminEmail = "admin@gmail.com";

    private String adminPassword = "12345678";

    private double initialBalance = 5_000_000.0;

    private double reserveBalance = 5_000_000.0;

    private double circulationBalance = 0.0;

    /**
     * Monto por wallet de estudiante. Debe cubrir compra de aspectos de seed (máx. ~1900).
     */
    private double walletSeedAmount = 3000.0;

    private String aspectImagesPath = "docs/aspects/";

    private String credentialsOutputPath = "docs/seed/credentials.md";

    private double aspectPriceCuerpo = 0.0;

    private double aspectPriceRemera = 500.0;

    private double aspectPriceSombrero = 300.0;
}
