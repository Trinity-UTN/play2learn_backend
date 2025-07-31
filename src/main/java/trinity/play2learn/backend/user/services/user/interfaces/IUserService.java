package trinity.play2learn.backend.user.services.user.interfaces;

import trinity.play2learn.backend.user.models.User;

public interface IUserService {

    User findUserByEmailAndPassword(String email, String password);

}
