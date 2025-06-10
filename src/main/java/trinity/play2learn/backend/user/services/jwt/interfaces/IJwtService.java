package trinity.play2learn.backend.user.services.jwt.interfaces;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {

    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateToken( Map<String, Object> extraClaims, UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
}
