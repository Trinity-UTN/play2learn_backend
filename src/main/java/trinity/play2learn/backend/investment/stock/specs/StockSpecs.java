package trinity.play2learn.backend.investment.stock.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.investment.stock.models.Stock;


public class StockSpecs {

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Stock> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro dinámico: cualquier campo = valor exacto 
    public static Specification<Stock> genericFilter(String campo, String valor) {
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