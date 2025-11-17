package trinity.play2learn.backend.user.services.user.interfaces;

import trinity.play2learn.backend.user.models.User;

public interface IUserGetByEmailService {
    
    User findUserByEmail(String email);

}
