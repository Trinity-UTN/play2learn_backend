package trinity.play2learn.backend.configs.seed.simulation.services.interfaces;

import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationRequestDto;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationResultDto;

public interface IDatabaseSimulationService {

    SimulationResultDto execute(SimulationRequestDto request);
}
