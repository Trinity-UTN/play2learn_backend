package trinity.play2learn.backend.investment.savingAccount.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

public class SavingAccountSpecs {

    // Filtro dinámico: cualquier campo = valor exacto 
    public static Specification<SavingAccount> genericFilter(String campo, String valor) {
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

    public static Specification<SavingAccount> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

}