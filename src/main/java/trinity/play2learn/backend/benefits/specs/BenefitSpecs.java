package trinity.play2learn.backend.benefits.specs;

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
    public static Specification<Benefit> genericFilter(String campo, String valor) {
        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(campo), valor);
            } catch (IllegalArgumentException e) {
                return cb.conjunction(); // no aplica ningún filtro
            }
        };
    }

}
