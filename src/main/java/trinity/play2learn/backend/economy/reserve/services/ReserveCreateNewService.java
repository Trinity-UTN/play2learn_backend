package trinity.play2learn.backend.economy.reserve.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveCreateNewService;

import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.mappers.ReserveMapper;
import trinity.play2learn.backend.economy.reserve.models.Reserve;

@Service
@AllArgsConstructor
public class ReserveCreateNewService implements IReserveCreateNewService {

    private final IReserveRepository reserveRepository;

    private final IReserveFindLastService reserveFindLastService;
    
    @Override
    public Reserve execute() {
        Reserve lastReserve = reserveFindLastService.get();
        return reserveRepository.save(ReserveMapper.toModel(lastReserve));
    }
}
