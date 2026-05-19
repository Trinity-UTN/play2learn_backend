package trinity.play2learn.backend.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.services.jwt.interfaces.IRefreshTokenService;

@RestController
@RequestMapping("/refresh")
@AllArgsConstructor
public class UserRefreshTokenController {
    
    private final IRefreshTokenService refreshTokenService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        this.refreshTokenService.refreshAccessToken(request, response);
        return ResponseFactory.noContent(
            SuccessfulMessages.refreshTokenSuccessfully()
        );
    }
}
