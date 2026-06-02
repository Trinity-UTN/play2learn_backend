package trinity.play2learn.backend.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.services.login.interfaces.IUserLogoutService;

@RestController
@RequestMapping("/user-logout")
@AllArgsConstructor
public class UserLogoutController {
    
    private final IUserLogoutService userLogoutService;

    @SessionRequired(roles ={Role.ROLE_ADMIN,Role.ROLE_STUDENT,Role.ROLE_TEACHER,Role.ROLE_DEV})
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> logout(HttpServletResponse response) {

        this.userLogoutService.logout(response);

        return ResponseFactory.noContent(SuccessfulMessages.logoutSuccessfully()); 
    }
}
