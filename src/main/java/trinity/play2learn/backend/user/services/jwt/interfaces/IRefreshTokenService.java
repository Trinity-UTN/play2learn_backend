package trinity.play2learn.backend.user.services.jwt.interfaces;

import trinity.play2learn.backend.user.dtos.token.AccessTokenDto;
import trinity.play2learn.backend.user.dtos.token.RefreshTokenDto;

public interface IRefreshTokenService {
    
    AccessTokenDto refreshAccessToken(RefreshTokenDto refreshTokenDto);
}
