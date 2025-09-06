package trinity.play2learn.backend.activity.activity.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public class ActivitySpecs {

    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Activity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Activity> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro dinámico: cualquier campo = valor exacto
    public static Specification<Activity> genericFilter(String field, String value) {
        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(field), value);
            } catch (IllegalArgumentException e) {

                return cb.conjunction(); // no aplica ningún filtro
            }
        };
    }

    public static Specification<Activity> hasSubjectId(Long subjectId) {
        return (root, query, cb) -> {
            if (subjectId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("subject").get("id"), subjectId);
        };
    }

}