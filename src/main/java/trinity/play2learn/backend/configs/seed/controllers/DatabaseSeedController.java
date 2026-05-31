package trinity.play2learn.backend.configs.seed.controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.configs.seed.dtos.SeedResultDto;
import trinity.play2learn.backend.configs.seed.services.interfaces.IDatabaseSeedService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.repository.IUserRepository;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dev")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DatabaseSeedController {

    private final IDatabaseSeedService databaseSeedService;

    private final IUserRepository userRepository;

    /**
     * Seed inicial sin autenticación. Solo permitido cuando no existen usuarios (BD vacía).
     */
    @PostMapping("/seed/bootstrap")
    public ResponseEntity<BaseResponse<SeedResultDto>> bootstrap() {
        if (userRepository.count() > 0) {
            throw new UnauthorizedException("Bootstrap solo permitido en base de datos vacía");
        }
        return ResponseFactory.created(databaseSeedService.execute(), "Seed de base de datos completado");
    }

    @PostMapping("/seed")
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<SeedResultDto>> seed() {
        SeedResultDto result = databaseSeedService.execute();
        return ResponseFactory.created(result, "Seed de base de datos completado");
    }
}
