package trinity.play2learn.backend.pruebaConfigs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@RequestMapping("/api/test/session")
public class TestSessionController {
    

    @GetMapping("/admin")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<String>> admin() {
        return ResponseFactory.ok("Todo correcto", "Respuesta OK");
    }

    @GetMapping("/dev")
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<String>> dev() {
        return ResponseFactory.ok("Todo correcto", "Respuesta OK");
    }

    @GetMapping("/teacher")
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<String>> teacher() {
        return ResponseFactory.ok("Todo correcto", "Respuesta OK");
    }

    @GetMapping("/student")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<String>> student() {
        return ResponseFactory.ok("Todo correcto", "Respuesta OK");
    }

    @GetMapping("/user")
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT, Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<User>> user(@SessionUser User user) {

        return ResponseFactory.ok(user, "Respuesta OK");
    }
}
