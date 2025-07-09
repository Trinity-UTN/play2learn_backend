package trinity.play2learn.backend.user.services.jwt;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.user.dtos.token.AccessTokenDto;
import trinity.play2learn.backend.user.dtos.token.RefreshTokenDto;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;
import trinity.play2learn.backend.user.services.jwt.interfaces.IRefreshTokenService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserFindService;

@Service
@AllArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private final IJwtService jwtService;
    private final IUserFindService userFindService;

    @Override
    public AccessTokenDto refreshAccessToken(RefreshTokenDto refreshTokenDto) {

        String refreshToken = refreshTokenDto.getRefreshToken();
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new UnauthorizedException("Refresh token expired.");
        }

        String email;
        try {
            email = jwtService.extractUsername(refreshToken);
            
        } catch (JwtException e) { //Si la firma del token es invalida, el metodo extractUsername lanza una excepcion (Que se propaga desde extractAllClaims)
            throw new UnauthorizedException("Invalid authentication refresh token.");
        }

        User user = userFindService.findUserByEmail(email);
        String accessToken = jwtService.generateAccessToken(user);

        AccessTokenDto accessTokenDto = AccessTokenDto
            .builder()
            .accessToken(accessToken)
            .build();

        return accessTokenDto;
    }
    
}
