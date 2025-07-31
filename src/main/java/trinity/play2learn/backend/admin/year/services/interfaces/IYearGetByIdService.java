package trinity.play2learn.backend.admin.year.services.interfaces;

import trinity.play2learn.backend.admin.year.models.Year;

public interface IYearGetByIdService {

    public Year findById (Long id);
    
}
