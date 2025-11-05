package trinity.play2learn.backend.benefits.specs;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.benefits.models.Benefit;

public class BenefitSpecs {

    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Benefit> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Benefit> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro dinámico: cualquier campo = valor exacto
    public static Specification<Benefit> genericFilter(String field, String value) {

        switch (field) {
            case "subjectId":
                try {
                    return hasSubjectId(Long.valueOf(value));
                } catch (Exception e) { // Evita un 500 si el valor no es un numero
                    return (root, query, cb) -> cb.conjunction();
                }

            case "state":
                return hasState(value);
        }

        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(field), value);
            } catch (IllegalArgumentException e) {
                return cb.conjunction(); // no aplica ningún filtro
            }
        };
    }

    public static Specification<Benefit> hasSubjectId(Long subjectId) {
        return (root, query, cb) -> {
            if (subjectId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("subject").get("id"), subjectId);
        };
    }

    public static Specification<Benefit> hasState(String state) {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();

            switch (state) {
                case "PUBLISHED":
                    return cb.greaterThanOrEqualTo(root.get("endAt"), now);

                case "EXPIRED":
                    return cb.lessThan(root.get("endAt"), now);

                default:
                    return cb.conjunction(); // si no matchea, no aplica filtro
            }
        };
    }

}
