package trinity.play2learn.backend.user.services.jwt.interfaces;


import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractRole(String token);

    boolean isTokenExpired(String token);
    
}
