package trinity.play2learn.backend.economy.reserve.services.interfaces;

import trinity.play2learn.backend.economy.reserve.models.Reserve;

public interface IReserveModifyService {
    
    public Reserve moveToReserve (Double amount, Reserve reserve);

    public Reserve moveToCirculation (Double amount, Reserve reserve);
    
}
