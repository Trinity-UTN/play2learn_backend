package trinity.play2learn.backend.configs.seed.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.configs.seed.config.DatabaseSeedProperties;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;

/**
 * Registra el catálogo de aspectos desde {@code docs/aspects/aspects.txt}.
 * Formato CSV (separador {@code ;}): tipo;nombre;url_imagen;precio
 */
@Slf4j
@Service
@AllArgsConstructor
public class AspectSeedService {

    private static final int EXPECTED_COLUMNS = 4;

    private final DatabaseSeedProperties properties;

    private final IAspectRepository aspectRepository;

    /**
     * Crea o reutiliza aspectos a partir del archivo CSV del catálogo.
     */
    public List<Aspect> seedCatalog(SeedDataCollector collector) throws IOException {
        Path catalogPath = Path.of(properties.getAspectCatalogPath()).toAbsolutePath().normalize();
        if (!Files.isRegularFile(catalogPath)) {
            throw new IOException("Catálogo de aspectos no encontrado: " + catalogPath);
        }

        List<Aspect> aspects = new ArrayList<>();
        List<String> lines = Files.readAllLines(catalogPath);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            int lineNumber = i + 1;
            try {
                AspectCatalogRow row = parseCatalogLine(line);
                aspects.add(registerAspect(row, collector));
            } catch (IllegalArgumentException ex) {
                log.warn("Línea {} omitida en catálogo de aspectos: {} — {}", lineNumber, line, ex.getMessage());
            }
        }

        log.info("Fase aspectos: {} aspectos en catálogo", aspects.size());
        return aspects;
    }

    static AspectCatalogRow parseCatalogLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException(
                "se esperaban %d columnas (tipo;nombre;url;precio), hay %d".formatted(EXPECTED_COLUMNS, parts.length));
        }

        TypeAspect type;
        try {
            type = TypeAspect.valueOf(parts[0].trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("tipo de aspecto inválido: " + parts[0].trim(), ex);
        }

        String name = unquote(parts[1].trim());
        String imageUrl = unquote(parts[2].trim());
        BigDecimal price = new BigDecimal(parts[3].trim());

        if (name.isBlank()) {
            throw new IllegalArgumentException("nombre vacío");
        }
        if (imageUrl.isBlank()) {
            throw new IllegalArgumentException("url de imagen vacía");
        }

        return new AspectCatalogRow(type, name, imageUrl, price);
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private Aspect registerAspect(AspectCatalogRow row, SeedDataCollector collector) {
        if (aspectRepository.existsByName(row.name())) {
            return aspectRepository.findAllByDeletedAtIsNullOrderByTypeAscNameAsc().stream()
                .filter(a -> row.name().equals(a.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aspecto existente no encontrado: " + row.name()));
        }

        Aspect aspect = Aspect.builder()
            .name(row.name())
            .image(row.imageUrl())
            .price(row.price())
            .type(row.type())
            .available(true)
            .build();

        Aspect saved = aspectRepository.save(aspect);
        collector.incrementAspects();
        log.debug("Aspecto registrado: {} ({})", row.name(), row.type());
        return saved;
    }

    record AspectCatalogRow(TypeAspect type, String name, String imageUrl, BigDecimal price) {}
}
