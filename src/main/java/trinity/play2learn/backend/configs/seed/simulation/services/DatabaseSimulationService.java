package trinity.play2learn.backend.configs.seed.simulation.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * Orquesta las fases de simulación académica y económica en un rango de fechas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DatabaseSimulationService implements IDatabaseSimulationService {

    private final SimulationProperties simulationProperties;
    private final ISubjectRepository subjectRepository;
    private final ITeacherRepository teacherRepository;
    private final ActivitySimulationGenerateService activityGenerateService;
    private final ActivitySimulationAttemptService activityAttemptService;
    private final BenefitSimulationGenerateService benefitGenerateService;
    private final BenefitSimulationLifecycleService benefitLifecycleService;
    private final AspectSimulationPurchaseService aspectPurchaseService;

    @Override
    public SimulationResultDto execute(SimulationRequestDto request) {
        SimulationDateValidator.validateRange(request.getFromDate(), request.getToDate());
        validatePrerequisites();

        SimulationTimeline timeline = new SimulationTimeline(
            request.getFromDate(), request.getToDate(), simulationProperties
        );
        SimulationDataCollector collector = new SimulationDataCollector();

        long startMs = System.currentTimeMillis();

        log.info("Fase 1/5: generación de actividades [{} - {}]", request.getFromDate(), request.getToDate());
        activityGenerateService.generate(timeline, collector);
        log.info("Fase 1 completada en {} ms — {} actividades", elapsed(startMs), collector.getActivityIds().size());

        startMs = System.currentTimeMillis();
        log.info("Fase 2/5: simulación de intentos");
        activityAttemptService.simulate(timeline, collector);
        log.info("Fase 2 completada en {} ms — {} intentos, {} aprobados", elapsed(startMs), collector.getAttempts(), collector.getApproved());

        startMs = System.currentTimeMillis();
        log.info("Fase 3/5: generación de beneficios");
        benefitGenerateService.generate(timeline, collector);
        log.info("Fase 3 completada en {} ms — {} beneficios", elapsed(startMs), collector.getBenefits().size());

        startMs = System.currentTimeMillis();
        log.info("Fase 4/5: ciclo de vida de beneficios");
        benefitLifecycleService.simulate(timeline, collector);
        log.info("Fase 4 completada en {} ms", elapsed(startMs));

        startMs = System.currentTimeMillis();
        log.info("Fase 5/5: compra de aspectos");
        aspectPurchaseService.simulate(timeline, collector);
        log.info("Fase 5 completada en {} ms", elapsed(startMs));

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
    }

    private long elapsed(long startMs) {
        return System.currentTimeMillis() - startMs;
    }
}
