package trinity.play2learn.backend.user.services.user.interfaces;

import trinity.play2learn.backend.user.models.User;

public interface IUserService {

    User findUserOrThrowException(String email, String password);

}
