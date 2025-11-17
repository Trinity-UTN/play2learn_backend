package trinity.play2learn.backend.investment.fixedTermDeposit.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;

public class FixedTermDepositSpecs {

    // Filtro dinámico: cualquier campo = valor exacto 
    public static Specification<FixedTermDeposit> genericFilter(String campo, String valor) {
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

    public static Specification<FixedTermDeposit> hasState(String stateValue) {
        return (root, query, cb) -> {
            try {
                FixedTermState state = FixedTermState.valueOf(stateValue.toUpperCase());
                return cb.equal(root.get("fixedTermState"), state);
            } catch (IllegalArgumentException e) {
                return cb.conjunction(); // si el valor no coincide con el enum, ignora el filtro
            }
        };
    }

    public static Specification<FixedTermDeposit> hasDays(String daysEnumValue) {
        return (root, query, cb) -> {
            try {
                FixedTermDays fixedTermDays = FixedTermDays.valueOf(daysEnumValue.toUpperCase());
                return cb.equal(root.get("fixedTermDays"), fixedTermDays);
            } catch (IllegalArgumentException e) {
                // Si el valor no corresponde a un enum válido, ignora el filtro
                return cb.conjunction();
            }
        };
    }

    public static Specification<FixedTermDeposit> hasWallet(Wallet wallet) {
        return (root, query, cb) -> cb.equal(root.get("wallet"), wallet);
    }
}