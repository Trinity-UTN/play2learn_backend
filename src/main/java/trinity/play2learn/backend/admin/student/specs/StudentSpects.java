package trinity.play2learn.backend.admin.student.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.student.models.Student;

public class StudentSpects {

    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Student> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Student> nameOrLastNameContains(String search) {
        return (root, query, cb) -> {
            String lowered = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), lowered),
                    cb.like(cb.lower(root.get("lastname")), lowered));
        };
    }

    // Filtro dinámico: cualquier campo = valor exacto
    public static Specification<Student> genericFilter(String field, String value) {

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