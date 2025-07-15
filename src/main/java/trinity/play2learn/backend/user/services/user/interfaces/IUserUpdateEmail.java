package trinity.play2learn.backend.user.services.user.interfaces;

import trinity.play2learn.backend.user.models.User;

public interface IUserUpdateEmail {
    
    void update (User user, String email);

}
