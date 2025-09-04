package trinity.play2learn.backend.economy.transaction.mappers;

import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public class TransactionMapper {
    

    public static Transaction toModel (
        Double amount,
        String description,
        TransactionActor origin,
        TransactionActor destination,
        Wallet wallet,
        Subject subject,
        Activity activity,
        Reserve reserve
    ){
        return Transaction.builder()
        .amount(amount)
        .description(description)
        .origin(origin)
        .destination(destination)
        .wallet(wallet)
        .subject(subject)
        .activity(activity)
        .reserve(reserve)
        .build();
    }
 
}
