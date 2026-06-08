package trinity.play2learn.backend.configs.seed.simulation.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.mappers.AhorcadoMapper;
import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;
import trinity.play2learn.backend.activity.ahorcado.repositories.IAhorcadoRepository;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.mappers.ClasificacionActivityMapper;
import trinity.play2learn.backend.activity.clasificacion.models.ClasificacionActivity;
import trinity.play2learn.backend.activity.clasificacion.repositories.IClasificacionActivityRepository;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.mappers.CompletarOracionActivityMapper;
import trinity.play2learn.backend.activity.completarOracion.models.CompletarOracionActivity;
import trinity.play2learn.backend.activity.completarOracion.repositories.ICompletarOracionRepository;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.mappers.EventMapper;
import trinity.play2learn.backend.activity.ordenarSecuencia.mappers.OrdenarSecuenciaMapper;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;
import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;
import trinity.play2learn.backend.activity.ordenarSecuencia.repositories.IOrdenarSecuenciaRepository;
import trinity.play2learn.backend.activity.preguntados.Mappers.PreguntadosMapper;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.models.Preguntados;
import trinity.play2learn.backend.activity.preguntados.repositories.IPreguntadosRepository;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.templates.SimulationActivityType;
import trinity.play2learn.backend.configs.seed.simulation.templates.SubjectActivityTemplateFactory;
import trinity.play2learn.backend.configs.seed.simulation.templates.SubjectActivityTemplateFactory.SimulationActivitySpec;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

/**
 * Genera actividades temáticas por materia dentro del rango simulado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivitySimulationGenerateService {

    private final ISubjectRepository subjectRepository;
    private final SubjectActivityTemplateFactory templateFactory;
    private final SimulationProperties properties;
    private final ITransactionGenerateService transactionGenerateService;
    private final IPreguntadosRepository preguntadosRepository;
    private final IAhorcadoRepository ahorcadoRepository;
    private final IClasificacionActivityRepository clasificacionRepository;
    private final ICompletarOracionRepository completarOracionRepository;
    private final IOrdenarSecuenciaRepository ordenarSecuenciaRepository;
    private final EntityManager entityManager;

    public void generate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Subject> subjects = subjectRepository.findAllByDeletedAtIsNull();
        int totalCreated = 0;

        for (Subject subject : subjects) {
            if (subject.getTeacher() == null) {
                log.warn("Materia {} sin docente, omitiendo actividades", subject.getName());
                continue;
            }

            int count = timeline.random().nextInt(
                properties.getMinActivitiesPerSubject(),
                properties.getMaxActivitiesPerSubject() + 1
            );

            log.info("Generando {} actividades para materia {}", count, subject.getName());

            for (int i = 0; i < count; i++) {
                if (totalCreated >= properties.getMaxActivitiesTotal()) {
                    log.info("Límite global de actividades alcanzado ({})", properties.getMaxActivitiesTotal());
                    return;
                }

                SimulationTimeline.ActivitySchedule schedule = timeline.scheduleActivity(totalCreated, properties.getMaxActivitiesTotal());
                double initialBalance = properties.getActivityInitialBalanceMin()
                    + timeline.random().nextInt(properties.getActivityInitialBalanceMax() - properties.getActivityInitialBalanceMin() + 1);

                if (subject.getActualBalance() == null || subject.getActualBalance() < initialBalance) {
                    log.warn("Saldo insuficiente en materia {} ({} < {}), omitiendo actividad",
                        subject.getName(), subject.getActualBalance(), initialBalance);
                    continue;
                }

                SimulationActivityType type = templateFactory.pickRandomType(subject.getName(), timeline.random());
                SimulationActivitySpec spec = templateFactory.createSpec(
                    subject, type, schedule.startDate(), schedule.endDate(), 3, initialBalance, timeline.random()
                );

                try {
                    Activity activity = persistActivity(subject, spec, schedule.createdAt());
                    registerActivityTransaction(subject, activity, initialBalance);
                    subject.setActualBalance(subject.getActualBalance() - initialBalance);
                    subjectRepository.save(subject);
                    activity.setActualBalance(initialBalance);
                    Long activityId = resolvePersistedId(activity);
                    if (activityId == null) {
                        log.warn("Actividad {} para {} sin id persistente, omitiendo del collector", type, subject.getName());
                        continue;
                    }
                    collector.addActivityId(activityId);
                    totalCreated++;
                } catch (Exception e) {
                    log.warn("No se pudo crear actividad {} para {}: {}", type, subject.getName(), e.getMessage());
                }
            }
        }
    }

    private Activity persistActivity(Subject subject, SimulationActivitySpec spec, LocalDateTime createdAt) {
        ActivityRequestDto dto = spec.requestDto();
        Activity activity = switch (spec.type()) {
            case PREGUNTADOS -> savePreguntados((PreguntadosRequestDto) dto, subject, createdAt);
            case AHORCADO -> saveAhorcado((AhorcadoRequestDto) dto, subject, createdAt);
            case CLASIFICACION -> saveClasificacion((ClasificacionActivityRequestDto) dto, subject, createdAt);
            case COMPLETAR_ORACION -> saveCompletarOracion((CompletarOracionActivityRequestDto) dto, subject, createdAt);
            case ORDENAR_SECUENCIA -> saveOrdenarSecuencia((OrdenarSecuenciaRequestDto) dto, subject, createdAt);
        };
        return activity;
    }

    private Preguntados savePreguntados(PreguntadosRequestDto dto, Subject subject, LocalDateTime createdAt) {
        Preguntados activity = preguntadosRepository.save(PreguntadosMapper.toModel(dto, subject));
        return backdateActivity(activity, createdAt);
    }

    private Ahorcado saveAhorcado(AhorcadoRequestDto dto, Subject subject, LocalDateTime createdAt) {
        Ahorcado activity = ahorcadoRepository.save(AhorcadoMapper.toModel(dto, subject));
        return backdateActivity(activity, createdAt);
    }

    private ClasificacionActivity saveClasificacion(ClasificacionActivityRequestDto dto, Subject subject, LocalDateTime createdAt) {
        ClasificacionActivity activity = clasificacionRepository.save(ClasificacionActivityMapper.toModel(dto, subject));
        return backdateActivity(activity, createdAt);
    }

    private CompletarOracionActivity saveCompletarOracion(CompletarOracionActivityRequestDto dto, Subject subject, LocalDateTime createdAt) {
        CompletarOracionActivity activity = CompletarOracionActivityMapper.toModel(dto, subject);
        activity.buildCompleteSentences();
        activity = completarOracionRepository.save(activity);
        return backdateActivity(activity, createdAt);
    }

    private OrdenarSecuencia saveOrdenarSecuencia(OrdenarSecuenciaRequestDto dto, Subject subject, LocalDateTime createdAt) {
        OrdenarSecuencia base = OrdenarSecuenciaMapper.toModel(dto, subject);
        List<Event> events = dto.getEvents().stream()
            .map(eventDto -> EventMapper.toModel(eventDto, base, null))
            .toList();
        base.setEvents(events);
        OrdenarSecuencia saved = ordenarSecuenciaRepository.save(base);
        return backdateActivity(saved, createdAt);
    }

    private <T extends Activity> T backdateActivity(T activity, LocalDateTime createdAt) {
        activity.setCreatedAt(createdAt);
        if (activity instanceof Preguntados p) {
            return (T) preguntadosRepository.save(p);
        }
        if (activity instanceof Ahorcado a) {
            return (T) ahorcadoRepository.save(a);
        }
        if (activity instanceof ClasificacionActivity c) {
            return (T) clasificacionRepository.save(c);
        }
        if (activity instanceof CompletarOracionActivity co) {
            return (T) completarOracionRepository.save(co);
        }
        if (activity instanceof OrdenarSecuencia o) {
            return (T) ordenarSecuenciaRepository.save(o);
        }
        return activity;
    }

    private void registerActivityTransaction(Subject subject, Activity activity, double initialBalance) {
        transactionGenerateService.generate(
            TypeTransaction.ACTIVIDAD,
            initialBalance,
            "Actividad simulada: " + activity.getName(),
            TransactionActor.SISTEMA,
            TransactionActor.SISTEMA,
            null,
            subject,
            activity,
            null,
            null,
            null,
            null
        );
    }

    private Long resolvePersistedId(Activity activity) {
        return (Long) entityManager.getEntityManagerFactory()
            .getPersistenceUnitUtil()
            .getIdentifier(activity);
    }
}
