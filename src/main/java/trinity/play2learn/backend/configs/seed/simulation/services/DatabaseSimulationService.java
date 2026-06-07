package trinity.play2learn.backend.configs.seed.simulation.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationRequestDto;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationResultDto;
import trinity.play2learn.backend.configs.seed.simulation.services.interfaces.IDatabaseSimulationService;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationDateValidator;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;

/**
 * Orquesta las fases de simulación académica, económica e inversiones en un rango de fechas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DatabaseSimulationService implements IDatabaseSimulationService {

    private static final int TOTAL_PHASES = 8;

    private final SimulationProperties simulationProperties;
    private final ISubjectRepository subjectRepository;
    private final ITeacherRepository teacherRepository;
    private final IStudentRepository studentRepository;
    private final ActivitySimulationGenerateService activityGenerateService;
    private final ActivitySimulationAttemptService activityAttemptService;
    private final BenefitSimulationGenerateService benefitGenerateService;
    private final BenefitSimulationLifecycleService benefitLifecycleService;
    private final AspectSimulationPurchaseService aspectPurchaseService;
    private final StockSimulationCatalogService stockCatalogService;
    private final SavingAccountSimulationService savingAccountSimulationService;
    private final FixedTermDepositSimulationService fixedTermDepositSimulationService;
    private final StockTradeSimulationService stockTradeSimulationService;
    private final StockHistorySimulationService stockHistorySimulationService;

    @Override
    public SimulationResultDto execute(SimulationRequestDto request) {
        SimulationDateValidator.validateRange(request.getFromDate(), request.getToDate());
        validatePrerequisites();

        SimulationTimeline timeline = new SimulationTimeline(
            request.getFromDate(), request.getToDate(), simulationProperties
        );
        SimulationDataCollector collector = new SimulationDataCollector();

        long startMs = System.currentTimeMillis();

        log.info("Fase 1/{}: generación de actividades [{} - {}]", TOTAL_PHASES, request.getFromDate(), request.getToDate());
        activityGenerateService.generate(timeline, collector);
        log.info("Fase 1 completada en {} ms — {} actividades", elapsed(startMs), collector.getActivityIds().size());

        startMs = System.currentTimeMillis();
        log.info("Fase 2/{}: simulación de intentos", TOTAL_PHASES);
        activityAttemptService.simulate(timeline, collector);
        log.info("Fase 2 completada en {} ms — {} intentos, {} aprobados", elapsed(startMs), collector.getAttempts(), collector.getApproved());

        startMs = System.currentTimeMillis();
        log.info("Fase 3/{}: generación de beneficios", TOTAL_PHASES);
        benefitGenerateService.generate(timeline, collector);
        log.info("Fase 3 completada en {} ms — {} beneficios", elapsed(startMs), collector.getBenefits().size());

        startMs = System.currentTimeMillis();
        log.info("Fase 4/{}: ciclo de vida de beneficios", TOTAL_PHASES);
        benefitLifecycleService.simulate(timeline, collector);
        log.info("Fase 4 completada en {} ms", elapsed(startMs));

        startMs = System.currentTimeMillis();
        log.info("Fase 5/{}: compra de aspectos", TOTAL_PHASES);
        aspectPurchaseService.simulate(timeline, collector);
        log.info("Fase 5 completada en {} ms", elapsed(startMs));

        startMs = System.currentTimeMillis();
        log.info("Fase 6/{}: catálogo de stocks", TOTAL_PHASES);
        stockCatalogService.simulate(timeline, collector);
        log.info("Fase 6 completada en {} ms — {} stocks", elapsed(startMs), collector.getStocksCreated());

        startMs = System.currentTimeMillis();
        log.info("Fase 7a/{}: cajas de ahorro", TOTAL_PHASES);
        savingAccountSimulationService.simulate(timeline, collector);
        log.info("Fase 7a completada en {} ms — {} cajas", elapsed(startMs), collector.getSavingAccounts());

        startMs = System.currentTimeMillis();
        log.info("Fase 7b/{}: plazos fijos", TOTAL_PHASES);
        fixedTermDepositSimulationService.simulate(timeline, collector);
        log.info("Fase 7b completada en {} ms — {} plazos", elapsed(startMs), collector.getFixedTermDeposits());

        startMs = System.currentTimeMillis();
        log.info("Fase 7c/{}: compra/venta de acciones", TOTAL_PHASES);
        stockTradeSimulationService.simulate(timeline, collector);
        log.info("Fase 7c completada en {} ms — {} compras, {} ventas", elapsed(startMs), collector.getStockBuys(), collector.getStockSells());

        startMs = System.currentTimeMillis();
        log.info("Fase 8/{}: evolución diaria de precios", TOTAL_PHASES);
        stockHistorySimulationService.simulate(timeline, collector);
        log.info("Fase 8 completada en {} ms — {} historiales", elapsed(startMs), collector.getStockHistoryRecords());

        return SimulationResultDto.builder()
            .message("Simulación completada exitosamente")
            .fromDate(request.getFromDate())
            .toDate(request.getToDate())
            .counts(collector.toCounts())
            .build();
    }

    private void validatePrerequisites() {
        if (teacherRepository.count() == 0 || subjectRepository.findAllByDeletedAtIsNull().isEmpty()) {
            throw new BadRequestException(
                "Bootstrap no ejecutado: se requieren teachers y subjects. Ejecute POST /api/dev/seed primero."
            );
        }
        if (studentRepository.count() == 0) {
            throw new BadRequestException(
                "Bootstrap no ejecutado: se requieren estudiantes con wallets. Ejecute POST /api/dev/seed primero."
            );
        }
    }

    private long elapsed(long startMs) {
        return System.currentTimeMillis() - startMs;
    }
}
