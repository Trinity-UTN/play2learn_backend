package trinity.play2learn.backend.configs.seed.simulation.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.templates.BenefitTemplateFactory;
import trinity.play2learn.backend.configs.seed.simulation.templates.BenefitTemplateFactory.BenefitTemplate;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;

/**
 * Genera beneficios temáticos por materia.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BenefitSimulationGenerateService {

    private final ISubjectRepository subjectRepository;
    private final IBenefitRepository benefitRepository;
    private final BenefitTemplateFactory benefitTemplateFactory;
    private final SimulationProperties properties;

    public void generate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Subject> subjects = subjectRepository.findAllByDeletedAtIsNull();
        int totalBenefits = 0;

        for (Subject subject : subjects) {
            if (subject.getTeacher() == null) {
                continue;
            }

            int count = timeline.random().nextInt(
                properties.getMinBenefitsPerSubject(),
                properties.getMaxBenefitsPerSubject() + 1
            );

            log.info("Generando {} beneficios para materia {}", count, subject.getName());

            for (int i = 0; i < count; i++) {
                if (totalBenefits >= properties.getMaxBenefitsTotal()) {
                    log.info("Límite global de beneficios alcanzado ({})", properties.getMaxBenefitsTotal());
                    return;
                }

                BenefitTemplate template = benefitTemplateFactory.pick(subject.getName(), timeline.random());
                LocalDateTime createdAt = timeline.randomBetween(timeline.getFrom(), timeline.getTo().minusDays(7));
                LocalDateTime endAt = timeline.nextAfter(createdAt, timeline.getTo());

                long cost = clampCost(template.baseCost(), timeline);

                Benefit benefit = benefitRepository.save(Benefit.builder()
                    .name(template.name())
                    .description(template.description())
                    .cost(cost)
                    .purchaseLimit(20)
                    .purchaseLimitPerStudent(1)
                    .endAt(endAt)
                    .subject(subject)
                    .icon(template.icon())
                    .category(template.category())
                    .color(template.color())
                    .build());

                collector.addBenefit(benefit);
                totalBenefits++;
                log.debug("Beneficio creado: {} (${}) para {}", benefit.getName(), cost, subject.getName());
            }
        }
    }

    private long clampCost(long baseCost, SimulationTimeline timeline) {
        int min = properties.getBenefitCostMin();
        int max = properties.getBenefitCostMax();
        long jitter = baseCost + timeline.random().nextInt(50);
        return Math.max(min, Math.min(max, jitter));
    }
}
