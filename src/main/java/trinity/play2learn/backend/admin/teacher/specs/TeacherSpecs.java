package trinity.play2learn.backend.admin.teacher.specs;

import org.springframework.data.jpa.domain.Specification;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public class TeacherSpecs {

    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Teacher> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Filtro por búsqueda textual (ej: name LIKE %search% OR lastname LIKE %search%)
    public static Specification<Teacher> nameOrLastnameContains(String search) {
        return (root, query, cb) -> {
            String lowered = "%" + search.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), lowered),
                cb.like(cb.lower(root.get("lastname")), lowered)
            );
        };
    }

    // Filtro dinámico: cualquier campo = valor exacto (ej: dni = '12345678')
    public static Specification<Teacher> genericFilter(String field, String value) {
        return (root, query, cb) -> {
            try {
                return cb.equal(root.get(field), value);
            } catch (IllegalArgumentException e) {
                return cb.conjunction(); // No aplica ningún filtro si el campo no existe
            }
        };
    }
}
