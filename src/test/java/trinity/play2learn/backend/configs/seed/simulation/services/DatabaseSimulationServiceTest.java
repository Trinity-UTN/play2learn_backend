package trinity.play2learn.backend.configs.seed.simulation.services;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationRequestDto;

@ExtendWith(MockitoExtension.class)
class DatabaseSimulationServiceTest {

    @Mock
    private SimulationProperties simulationProperties;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ITeacherRepository teacherRepository;

    @Mock
    private ActivitySimulationGenerateService activityGenerateService;

    @Mock
    private ActivitySimulationAttemptService activityAttemptService;

    @Mock
    private BenefitSimulationGenerateService benefitGenerateService;

    @Mock
    private BenefitSimulationLifecycleService benefitLifecycleService;

    @Mock
    private AspectSimulationPurchaseService aspectPurchaseService;

    @InjectMocks
    private DatabaseSimulationService databaseSimulationService;

    @Test
    @DisplayName("Given valid request When executing Then runs phases in order")
    void execute_runsPhasesInOrder() {
        when(teacherRepository.count()).thenReturn(6L);
        when(subjectRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(Subject.builder().id(1L).name("Matemática").build()));
        when(simulationProperties.getRandomSeed()).thenReturn(42L);

        SimulationRequestDto request = SimulationRequestDto.builder()
            .fromDate(LocalDateTime.of(2025, 1, 1, 8, 0))
            .toDate(LocalDateTime.of(2025, 3, 31, 18, 0))
            .build();

        databaseSimulationService.execute(request);

        InOrder order = inOrder(
            activityGenerateService,
            activityAttemptService,
            benefitGenerateService,
            benefitLifecycleService,
            aspectPurchaseService
        );
        order.verify(activityGenerateService).generate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(activityAttemptService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(benefitGenerateService).generate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(benefitLifecycleService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(aspectPurchaseService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
