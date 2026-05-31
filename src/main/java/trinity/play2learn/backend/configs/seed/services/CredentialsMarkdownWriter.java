package trinity.play2learn.backend.configs.seed.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.seed.config.DatabaseSeedProperties;
import trinity.play2learn.backend.configs.seed.dtos.SeedCountsDto;
import trinity.play2learn.backend.configs.seed.dtos.SeedCredentialsDto;
import trinity.play2learn.backend.user.models.Role;

@Service
@AllArgsConstructor
public class CredentialsMarkdownWriter {

    private final DatabaseSeedProperties properties;

    /**
     * Genera el archivo markdown con credenciales de usuarios creados por el seed.
     */
    public String write(List<SeedCredentialsDto> credentials, SeedCountsDto counts) throws IOException {
        Path outputPath = Path.of(properties.getCredentialsOutputPath()).toAbsolutePath().normalize();
        Files.createDirectories(outputPath.getParent());

        String content = buildMarkdown(credentials, counts);
        Files.writeString(outputPath, content, StandardCharsets.UTF_8);
        return outputPath.toString();
    }

    private String buildMarkdown(List<SeedCredentialsDto> credentials, SeedCountsDto counts) {
        StringBuilder md = new StringBuilder();
        md.append("# Credenciales de datos de prueba (Seed)\n\n");
        md.append("Generado: ")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .append("\n\n");
        md.append("> **Solo desarrollo.** No commitear este archivo. Contraseña de docentes y estudiantes = DNI.\n\n");

        md.append("## Resumen de entidades\n\n");
        md.append("| Entidad | Cantidad |\n|---------|----------|\n");
        md.append("| Reserves | ").append(counts.getReserves()).append(" |\n");
        md.append("| Years | ").append(counts.getYears()).append(" |\n");
        md.append("| Courses | ").append(counts.getCourses()).append(" |\n");
        md.append("| Teachers | ").append(counts.getTeachers()).append(" |\n");
        md.append("| Students | ").append(counts.getStudents()).append(" |\n");
        md.append("| Subjects | ").append(counts.getSubjects()).append(" |\n");
        md.append("| Aspects | ").append(counts.getAspects()).append(" |\n\n");

        appendSystemUsers(md, credentials);
        appendTable(md, "Docentes", credentials, Role.ROLE_TEACHER.name(), true);
        appendTable(md, "Estudiantes", credentials, Role.ROLE_STUDENT.name(), false);

        return md.toString();
    }

    private void appendSystemUsers(StringBuilder md, List<SeedCredentialsDto> credentials) {
        md.append("## Usuarios del sistema (DEV y ADMIN)\n\n");
        md.append("| Rol | Email | Contraseña |\n|-----|-------|------------|\n");
        credentials.stream()
            .filter(c -> Role.ROLE_DEV.name().equals(c.getRole()) || Role.ROLE_ADMIN.name().equals(c.getRole()))
            .sorted(Comparator.comparing(SeedCredentialsDto::getRole))
            .forEach(c -> md.append("| ")
                .append(c.getRole().replace("ROLE_", ""))
                .append(" | ").append(c.getEmail())
                .append(" | ").append(c.getPassword())
                .append(" |\n"));
        md.append("\n");
    }

    private void appendTable(
        StringBuilder md,
        String title,
        List<SeedCredentialsDto> credentials,
        String role,
        boolean teacherTable
    ) {
        md.append("## ").append(title).append("\n\n");
        if (teacherTable) {
            md.append("| Nombre | Email | DNI (contraseña) |\n|--------|-------|------------------|\n");
        } else {
            md.append("| Nombre | Email | DNI (contraseña) | Año | Curso |\n|--------|-------|------------------|-----|-------|\n");
        }

        credentials.stream()
            .filter(c -> role.equals(c.getRole()))
            .sorted(Comparator.comparing(SeedCredentialsDto::getEmail))
            .forEach(c -> {
                md.append("| ").append(nullToDash(c.getName()))
                    .append(" | ").append(c.getEmail())
                    .append(" | ").append(c.getDni());
                if (!teacherTable) {
                    md.append(" | ").append(nullToDash(c.getYearName()))
                        .append(" | ").append(nullToDash(c.getCourseName()));
                }
                md.append(" |\n");
            });
        md.append("\n");
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
