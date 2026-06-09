package trinity.play2learn.backend.configs.seed.simulation.controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationRequestDto;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationResultDto;
import trinity.play2learn.backend.configs.seed.simulation.services.interfaces.IDatabaseSimulationService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dev/seed")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class SimulationSeedController {

    private final IDatabaseSimulationService databaseSimulationService;

    @PostMapping("/simulate")
    //@SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<SimulationResultDto>> simulate(
        @RequestBody @Valid SimulationRequestDto request
    ) {
        SimulationResultDto result = databaseSimulationService.execute(request);
        return ResponseFactory.created(result, result.getMessage());
    }
}
