package trinity.play2learn.backend.economy.transaccion.services.interfaces;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.models.TypeTransaccion;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface ITransaccionGenerateService {
    
    public Transaccion generate (
        TypeTransaccion type,
        Double amount,
        String description,
        ActorTransaccion origin,
        ActorTransaccion destination,
        Wallet wallet,
        Subject subject
    );
}
