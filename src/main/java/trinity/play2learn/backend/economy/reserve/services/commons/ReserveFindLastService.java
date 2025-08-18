package trinity.play2learn.backend.economy.reserve.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;

@Service
@AllArgsConstructor
public class ReserveFindLastService implements IReserveFindLastService{
    
    private final IReserveRepository reserveRepository;

    @Override
    public Reserve get() {
        return reserveRepository.findFirstByOrderByCreatedAtDesc().orElseThrow(( ) -> new NotFoundException(
                NotFoundExceptionMesagges.resourceNotFoundById("Reserva", "ultima")
            ));
    }
    
}
