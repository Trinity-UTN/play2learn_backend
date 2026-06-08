package trinity.play2learn.backend.configs.seed.simulation.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.configs.levels.ValueXp;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationDateValidator;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileUpdateLevelService;

/**
 * Simula compra, solicitud y aceptación parcial de beneficios.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BenefitSimulationLifecycleService {

    private final SimulationProperties properties;
    private final ISubjectRepository subjectRepository;
    private final IBenefitRepository benefitRepository;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;
    private final ITransactionGenerateService transactionGenerateService;
    private final IProfileUpdateLevelService profileUpdateLevelService;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        int purchases = 0;
        int requests = 0;
        int accepted = 0;
        
        for (Benefit benefit : collector.getBenefits()) {
            Subject subject = benefit.getSubject();
            List<Student> students = subjectRepository.findStudentsBySubjectId(subject.getId());
            if (students.isEmpty()) {
                continue;
            }

            List<Student> buyers = selectSubset(students, properties.getStudentsBuyingBenefitRate(), timeline);
            LocalDateTime benefitCreatedAt = timeline.randomBetween(timeline.getFrom(), benefit.getEndAt().minusDays(1));
            int purchaseNumberForBenefit = 0;

            for (Student student : buyers) {
                if (benefit.getPurchasesLeft() != null && benefit.getPurchasesLeft() <= 0) {
                    break;
                }

                purchaseNumberForBenefit++;
                double cost = benefit.getCost().doubleValue();
                if (student.getWallet().getBalance() < cost) {
                    continue;
                }
                if (benefit.getEndAt().isBefore(timeline.getFrom())) {
                    continue;
                }

                LocalDateTime purchasedAt = timeline.nextAfter(benefitCreatedAt, benefit.getEndAt());
                SimulationDateValidator.validateBenefitLifecycle(benefitCreatedAt, benefit.getEndAt(), purchasedAt, null);

                transactionGenerateService.generate(
                    TypeTransaction.COMPRA,
                    cost,
                    "Compra simulada de beneficio",
                    TransactionActor.ESTUDIANTE,
                    TransactionActor.SISTEMA,
                    student.getWallet(),
                    null,
                    null,
                    benefit,
                    null,
                    null,
                    null
                );

                benefit.decrementPurchasesLeft();
                benefitRepository.save(benefit);

                BenefitPurchase purchase = benefitPurchaseRepository.save(
                    BenefitPurchaseMapper.toModel(benefit, student, purchaseNumberForBenefit)
                );
                purchase.setPurchasedAt(purchasedAt);
                purchase = benefitPurchaseRepository.save(purchase);
                collector.incrementBenefitPurchases();
                purchases++;

                profileUpdateLevelService.execute(student.getProfile(), ValueXp.BENEFIT_BOUGHT.getValue());

                if (timeline.random().nextDouble() >= properties.getPurchasersRequestingUseRate()) {
                    continue;
                }

                LocalDateTime requestedAt = timeline.nextAfter(purchasedAt, benefit.getEndAt());
                purchase.setState(BenefitPurchaseState.USE_REQUESTED);
                purchase = benefitPurchaseRepository.save(purchase);
                collector.incrementBenefitUseRequests();
                requests++;

                if (timeline.random().nextDouble() >= properties.getRequestsAcceptedRate()) {
                    continue;
                }

                LocalDateTime usedAt = timeline.nextAfter(requestedAt, benefit.getEndAt());
                SimulationDateValidator.validateBenefitLifecycle(benefitCreatedAt, benefit.getEndAt(), purchasedAt, usedAt);
                purchase.setState(BenefitPurchaseState.USED);
                purchase.setUsedAt(usedAt);
                benefitPurchaseRepository.save(purchase);
                collector.incrementBenefitUsesAccepted();
                accepted++;
            }
        }

        log.info("Beneficios: {} compras, {} solicitudes de uso, {} aceptadas", purchases, requests, accepted);
    }

    private List<Student> selectSubset(List<Student> students, double rate, SimulationTimeline timeline) {
        List<Student> shuffled = new ArrayList<>(students);
        Collections.shuffle(shuffled, timeline.random());
        int count = Math.max(0, (int) Math.round(students.size() * rate));
        count = Math.min(count, shuffled.size());
        return shuffled.subList(0, count);
    }
}
