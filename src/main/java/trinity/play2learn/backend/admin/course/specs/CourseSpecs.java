package trinity.play2learn.backend.admin.course.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.course.models.Course;

public class CourseSpecs {
    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Course> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Course> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro dinámico: cualquier campo = valor exacto (ej: Course_type = 'primario')
    public static Specification<Course> genericFilter (String field, String value) {
        
        if ("active".equalsIgnoreCase(field)) {
            boolean isActive = Boolean.parseBoolean(value);
            return (root, query, cb) -> isActive
                    ? cb.isNull(root.get("deletedAt"))
                    : cb.isNotNull(root.get("deletedAt")); 
        }

        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(field), value);
            } catch (IllegalArgumentException e) {
                // Esto es útil si querés ignorar filtros inválidos silenciosamente
                return cb.conjunction(); // no aplica ningún filtro
            }
        };
    }
}
