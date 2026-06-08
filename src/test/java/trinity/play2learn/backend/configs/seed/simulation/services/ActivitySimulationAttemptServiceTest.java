package trinity.play2learn.backend.configs.seed.simulation.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.services.internal.SimulationActivityCompletionService;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;

@ExtendWith(MockitoExtension.class)
class ActivitySimulationAttemptServiceTest {

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private IActivityRepository activityRepository;

    @Mock
    private SimulationActivityCompletionService completionService;

    @InjectMocks
    private ActivitySimulationAttemptService attemptService;

    private SimulationProperties properties;
    private SimulationTimeline timeline;

    @BeforeEach
    void setUp() {
        properties = new SimulationProperties();
        properties.setRandomSeed(42L);
        properties.setPassOnAttempt1Rate(0.35);
        properties.setPassOnAttempt2Rate(0.30);
        properties.setPassOnAttempt3Rate(0.20);
        properties.setFailAllAttemptsRate(0.15);

        timeline = new SimulationTimeline(
            LocalDateTime.of(2025, 1, 1, 8, 0),
            LocalDateTime.of(2025, 3, 31, 18, 0),
            properties
        );

        attemptService = new ActivitySimulationAttemptService(properties, subjectRepository, activityRepository, completionService);
    }

    @Test
    @DisplayName("Given timeline When resolving pass attempt Then returns valid outcome")
    void resolvePassAttempt_returnsValidOutcome() {
        int outcome = attemptService.resolvePassAttempt(timeline);
        assertThat(outcome).isIn(1, 2, 3, -1);
    }
}
