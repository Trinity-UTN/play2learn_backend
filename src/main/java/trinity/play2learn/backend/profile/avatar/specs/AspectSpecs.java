package trinity.play2learn.backend.profile.avatar.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;


public class AspectSpecs {
    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Aspect> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Filtro: solo disponibles
    public static Specification<Aspect> onlyAvailable() {
        return (root, query, cb) -> cb.isTrue(root.get("available"));
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Aspect> nameContains(String search) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro por tipo (enum)
    public static Specification<Aspect> typeEquals(String typeValue) {
        return (root, query, cb) -> {
            try {
                TypeAspect type = TypeAspect.valueOf(typeValue.toUpperCase());
                return cb.equal(root.get("type"), type);
            } catch (IllegalArgumentException e) {
                // Si el valor no coincide con un enum válido, no se aplica el filtro
                return cb.conjunction();
            }
        };
    }

    // Filtro genérico: cualquier campo = valor exacto
    public static Specification<Aspect> genericFilter(String campo, String valor) {
        return (root, query, cb) -> {
            try {
                return cb.equal(root.get(campo), valor);
            } catch (IllegalArgumentException e) {
                return cb.conjunction();
            }
        };
    }
}

