package trinity.play2learn.backend.admin.classes.services.interfaces;

import trinity.play2learn.backend.admin.classes.models.Class;

public interface IClassGetByIdService {
    
    public Class get(Long id);
}
