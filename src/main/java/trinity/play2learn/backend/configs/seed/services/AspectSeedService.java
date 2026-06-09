package trinity.play2learn.backend.configs.seed.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.configs.seed.config.DatabaseSeedProperties;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;

/**
 * Registra el catálogo de aspectos desde {@code docs/aspects/} sin depender de ImgBB.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AspectSeedService {

    private static final List<String> IMAGE_EXTENSIONS = List.of(".png", ".PNG");

    private final DatabaseSeedProperties properties;

    private final IAspectRepository aspectRepository;

    /**
     * Crea o reutiliza aspectos a partir de las imágenes en subcarpetas CUERPO, REMERA y SOMBRERO.
     */
    public List<Aspect> seedCatalog(SeedDataCollector collector) throws IOException {
        Path basePath = Path.of(properties.getAspectImagesPath()).toAbsolutePath().normalize();
        if (!Files.isDirectory(basePath)) {
            throw new IOException("Directorio de aspectos no encontrado: " + basePath);
        }

        List<Aspect> aspects = new ArrayList<>();

        for (TypeAspect type : TypeAspect.values()) {
            Path typeDir = basePath.resolve(type.name());
            if (!Files.isDirectory(typeDir)) {
                log.warn("Carpeta de aspectos omitida (no existe): {}", typeDir);
                continue;
            }

            try (Stream<Path> files = Files.list(typeDir)) {
                files.filter(Files::isRegularFile)
                    .filter(this::isImageFile)
                    .forEach(file -> aspects.add(registerAspect(file, type, collector)));
            }
        }

        log.info("Fase aspectos: {} aspectos en catálogo", aspects.size());
        return aspects;
    }

    private Aspect registerAspect(Path file, TypeAspect type, SeedDataCollector collector) {
        String name = filenameToAspectName(file.getFileName().toString());

        if (aspectRepository.existsByName(name)) {
            return aspectRepository.findAllByDeletedAtIsNullOrderByTypeAscNameAsc().stream()
                .filter(a -> name.equals(a.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aspecto existente no encontrado: " + name));
        }

        String imageRef = "seed://" + type.name() + "/" + file.getFileName().toString();

        Aspect aspect = Aspect.builder()
            .name(name)
            .image(imageRef)
            .price(BigDecimal.valueOf(resolvePrice(type)))
            .type(type)
            .available(true)
            .build();

        Aspect saved = aspectRepository.save(aspect);
        collector.incrementAspects();
        log.debug("Aspecto registrado: {} ({})", name, type);
        return saved;
    }

    private double resolvePrice(TypeAspect type) {
        return switch (type) {
            case CUERPO -> properties.getAspectPriceCuerpo();
            case REMERA -> properties.getAspectPriceRemera();
            case SOMBRERO -> properties.getAspectPriceSombrero();
        };
    }

    private boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString();
        return IMAGE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    /**
     * Convierte el nombre de archivo a un nombre legible válido para {@link Aspect#getName()}.
     */
    static String filenameToAspectName(String filename) {
        String base = filename;
        for (String ext : IMAGE_EXTENSIONS) {
            if (base.endsWith(ext)) {
                base = base.substring(0, base.length() - ext.length());
            }
        }
        String spaced = base
            .replace('_', ' ')
            .replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
            .trim();
        if (spaced.length() > 50) {
            spaced = spaced.substring(0, 50).trim();
        }
        return spaced;
    }
}
