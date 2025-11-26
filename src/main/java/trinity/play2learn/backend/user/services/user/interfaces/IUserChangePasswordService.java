package trinity.play2learn.backend.user.services.user.interfaces;

import trinity.play2learn.backend.user.models.User;

public interface IUserChangePasswordService {
    
    void changePassword(User user, String oldPassword, String newPassword);

}
