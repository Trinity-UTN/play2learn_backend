package trinity.play2learn.backend.benefits.specs;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;

public class BenefitPurchasesSpecs {

    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<BenefitPurchase> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // Busca si la cadena esta contenida en el nombre del estudiante que compro el beneficio
    public static Specification<BenefitPurchase> studentFullNameContains(String search) {
    return (root, query, cb) -> {
        String searchLower = "%" + search.toLowerCase() + "%";

        // Hacemos un join con student
        Join<Object, Object> studentJoin = root.join("student");

        // Concatenamos nombre + espacio + apellido
        Expression<String> fullName = cb.concat(
            cb.lower(studentJoin.get("name")),
            cb.concat(" ", cb.lower(studentJoin.get("lastname")))
        );

        // Verificamos si el string está contenido en la concatenación
        return cb.like(fullName, searchLower);
    };
}

    // Filtro dinámico: cualquier campo = valor exacto
    public static Specification<BenefitPurchase> genericFilter(String field, String value) {

        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(field), value);
            } catch (IllegalArgumentException e) {
                return cb.conjunction(); // no aplica ningún filtro
            }
        };
    }
}
