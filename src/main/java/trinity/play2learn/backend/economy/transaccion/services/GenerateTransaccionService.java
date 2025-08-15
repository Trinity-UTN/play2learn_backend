package trinity.play2learn.backend.economy.transaccion.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.models.TypeTransaccion;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.IGenerateTransaccionService;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.IStrategyTransaccionService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service
@AllArgsConstructor
public class GenerateTransaccionService implements IGenerateTransaccionService{

    private final Map<String, IStrategyTransaccionService> strategies;


    @Override
    @Transactional
    public Transaccion generate(
        TypeTransaccion type,
        Double amount, 
        String description, 
        ActorTransaccion origin, 
        ActorTransaccion destination, 
        Wallet wallet, 
        Subject subject) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        IStrategyTransaccionService strategy = strategies.get(type.name());

        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de transacción no soportado: " + type);
        }

        Transaccion transaccion = strategy.execute(
            amount, 
            description, 
            origin, 
            destination,
            wallet, 
            subject
        );

        return transaccion;

    }
    
}
