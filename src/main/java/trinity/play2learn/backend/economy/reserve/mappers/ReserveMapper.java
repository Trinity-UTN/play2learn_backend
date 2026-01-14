package trinity.play2learn.backend.economy.reserve.mappers;

import trinity.play2learn.backend.economy.reserve.models.Reserve;

public class ReserveMapper {
    
    public static Reserve toModel (Reserve lastReserve) {
        return Reserve.builder()
        .reserveBalance(lastReserve.getReserveBalance())
        .circulationBalance(lastReserve.getCirculationBalance())
        .initialBalance(lastReserve.getInitialBalance())
        .build();
    }
}
