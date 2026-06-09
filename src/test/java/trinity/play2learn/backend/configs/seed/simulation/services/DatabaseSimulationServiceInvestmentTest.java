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

import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationRequestDto;

@ExtendWith(MockitoExtension.class)
class DatabaseSimulationServiceInvestmentTest {

    @Mock
    private SimulationProperties simulationProperties;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ITeacherRepository teacherRepository;

    @Mock
    private IStudentRepository studentRepository;

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

    @Mock
    private StockSimulationCatalogService stockCatalogService;

    @Mock
    private SavingAccountSimulationService savingAccountSimulationService;

    @Mock
    private FixedTermDepositSimulationService fixedTermDepositSimulationService;

    @Mock
    private StockTradeSimulationService stockTradeSimulationService;

    @Mock
    private StockHistorySimulationService stockHistorySimulationService;

    @InjectMocks
    private DatabaseSimulationService databaseSimulationService;

    @Test
    @DisplayName("Given valid request When executing Then runs all 8 phases in order")
    void execute_runsAllPhasesInOrder() {
        when(teacherRepository.count()).thenReturn(6L);
        when(studentRepository.count()).thenReturn(180L);
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
            aspectPurchaseService,
            stockCatalogService,
            savingAccountSimulationService,
            fixedTermDepositSimulationService,
            stockTradeSimulationService,
            stockHistorySimulationService
        );
        order.verify(activityGenerateService).generate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(activityAttemptService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(benefitGenerateService).generate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(benefitLifecycleService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(aspectPurchaseService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(stockCatalogService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(savingAccountSimulationService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(fixedTermDepositSimulationService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(stockTradeSimulationService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        order.verify(stockHistorySimulationService).simulate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
