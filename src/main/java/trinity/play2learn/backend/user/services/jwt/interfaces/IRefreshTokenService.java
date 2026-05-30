package trinity.play2learn.backend.user.services.jwt.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IRefreshTokenService {
    
    void refreshAccessToken( HttpServletRequest request, HttpServletResponse response);
}
