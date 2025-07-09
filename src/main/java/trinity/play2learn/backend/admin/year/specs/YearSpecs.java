package trinity.play2learn.backend.admin.year.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.year.models.Year;

public class YearSpecs {
    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Year> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Year> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro dinámico: cualquier campo = valor exacto (ej: Year_type = 'primario')
    public static Specification<Year> genericFilter (String campo, String valor) {
        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(campo), valor);
            } catch (IllegalArgumentException e) {
                // Esto es útil si querés ignorar filtros inválidos silenciosamente
                return cb.conjunction(); // no aplica ningún filtro
            }
        };
    }
}
