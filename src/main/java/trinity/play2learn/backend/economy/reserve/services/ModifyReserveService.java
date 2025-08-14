package trinity.play2learn.backend.economy.reserve.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IFindLastReserveService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IModifyReserveService;

@Service
@AllArgsConstructor
public class ModifyReserveService implements IModifyReserveService{

    private final IReserveRepository reserveRepository;

    private final IFindLastReserveService findLastReserveService;

    @Override
    public void moveToReserve(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        Reserve reserve = findLastReserveService.get();

        if (amount > reserve.getCirculationBalance()){
            throw new IllegalArgumentException("No hay suficiente dinero en la circulacion");
        }

        reserve.setCirculationBalance(reserve.getCirculationBalance() - amount);
        reserve.setReserveBalance(reserve.getReserveBalance() + amount);
        reserve.setLastUpdateAt(LocalDateTime.now());

        reserveRepository.save(reserve);
    }

    @Override
    public void moveToCirculation(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        
        Reserve reserve = findLastReserveService.get();

        if (amount > reserve.getReserveBalance()){
            throw new IllegalArgumentException("No hay suficiente dinero en la reserva");
        }

        reserve.setReserveBalance(reserve.getReserveBalance() - amount);
        reserve.setCirculationBalance(reserve.getCirculationBalance() + amount);
        reserve.setLastUpdateAt(LocalDateTime.now());

        reserveRepository.save(reserve);
    }

    
    
}
