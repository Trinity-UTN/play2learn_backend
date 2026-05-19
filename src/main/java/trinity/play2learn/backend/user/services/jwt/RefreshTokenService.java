package trinity.play2learn.backend.user.services.jwt;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;
import trinity.play2learn.backend.user.services.jwt.interfaces.IRefreshTokenService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserGetByEmailService;

@Service
@AllArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private final IJwtService jwtService;
    private final IUserGetByEmailService userFindService;

    @Override
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        // Leer el refresh token de la cookie
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            refreshToken = Arrays.stream(cookies)
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        }

        if (refreshToken == null) {
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.INVALID_REFRESH_TOKEN
            );
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.TOKEN_EXPIRED
            );
        }

        String email;
        try {
            email = jwtService.extractUsername(refreshToken);
        } catch (JwtException e) {
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.INVALID_REFRESH_TOKEN
            );
        }

        User user = userFindService.findUserByEmail(email);
        String accessToken = jwtService.generateAccessToken(user);

        // Setear el nuevo access token en la cookie
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(15))
            .sameSite("Strict")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }
}
