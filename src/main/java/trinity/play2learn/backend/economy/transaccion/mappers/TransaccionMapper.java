package trinity.play2learn.backend.economy.transaccion.mappers;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public class TransaccionMapper {
    

    public static Transaccion toModel (
        Double amount,
        String description,
        ActorTransaccion origin,
        ActorTransaccion destination,
        Wallet wallet,
        Subject subject,
        Reserve reserve
    ){
        return Transaccion.builder()
        .amount(amount)
        .description(description)
        .origin(origin)
        .destination(destination)
        .wallet(wallet)
        .subject(subject)
        .reserve(reserve)
        .build();
    }
 
}
