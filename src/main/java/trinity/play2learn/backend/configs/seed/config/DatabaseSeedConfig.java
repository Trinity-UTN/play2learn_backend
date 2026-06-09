package trinity.play2learn.backend.configs.seed.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;

@Configuration
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@EnableConfigurationProperties({DatabaseSeedProperties.class, SimulationProperties.class})
public class DatabaseSeedConfig {
}
