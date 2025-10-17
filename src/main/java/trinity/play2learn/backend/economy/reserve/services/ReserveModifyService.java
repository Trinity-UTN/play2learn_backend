package trinity.play2learn.backend.economy.reserve.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;

@Service
@AllArgsConstructor
public class ReserveModifyService implements IReserveModifyService {

    private final IReserveRepository reserveRepository;

    @Override
    public Reserve moveToReserve(Double amount, Reserve reserve) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        if (amount > reserve.getCirculationBalance()){
            throw new IllegalArgumentException("No hay suficiente dinero en la circulacion");
        }

        reserve.setCirculationBalance(reserve.getCirculationBalance() - amount);
        reserve.setReserveBalance(reserve.getReserveBalance() + amount);
        reserve.setLastUpdateAt(LocalDateTime.now());

        return reserveRepository.save(reserve);
    }

    @Override
    public Reserve moveToCirculation(Double amount, Reserve reserve) {
        if (amount <= 0) {
            throw new IllegalArgumentException(EconomyMessages.AMOUNT_MAJOR_TO_0);
        }

        if (amount > reserve.getReserveBalance()){
            throw new IllegalArgumentException(EconomyMessages.NOT_ENOUGH_RESERVE_MONEY);
        }

        reserve.setReserveBalance(reserve.getReserveBalance() - amount);
        reserve.setCirculationBalance(reserve.getCirculationBalance() + amount);

        return reserveRepository.save(reserve);
    }

    
    
}
