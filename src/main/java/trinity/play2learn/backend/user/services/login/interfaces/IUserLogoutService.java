package trinity.play2learn.backend.user.services.login.interfaces;

import jakarta.servlet.http.HttpServletResponse;

public interface IUserLogoutService {
    
    void logout(HttpServletResponse response);
}
