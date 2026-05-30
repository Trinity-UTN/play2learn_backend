package trinity.play2learn.backend.user.services.login;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.services.login.interfaces.IUserLogoutService;

@Service
@AllArgsConstructor
public class UserLogoutService implements IUserLogoutService{
    
    @Override
    public void logout(HttpServletResponse response) {
        
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")  
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(true)
            .path("/refresh") 
            .maxAge(0)
            .sameSite("Strict")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
