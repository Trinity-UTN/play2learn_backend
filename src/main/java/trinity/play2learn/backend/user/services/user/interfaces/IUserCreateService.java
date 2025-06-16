package trinity.play2learn.backend.user.services.user.interfaces;

import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

public interface IUserCreateService {
    
    public User create (String email, String password, Role Role);
}
