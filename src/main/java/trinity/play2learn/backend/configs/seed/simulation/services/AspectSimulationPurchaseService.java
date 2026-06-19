package trinity.play2learn.backend.configs.seed.simulation.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.configs.levels.ValueXp;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileUpdateLevelService;

/**
 * Simula compra parcial de skins de pago (REMERA/SOMBRERO) no poseídos por el estudiante.
 * El bootstrap asigna 1 CUERPO + 1 REMERA + 1 SOMBRERO equipados; aquí se compran piezas adicionales.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AspectSimulationPurchaseService {

    private final IStudentRepository studentRepository;
    private final IAspectRepository aspectRepository;
    private final IProfileRepository profileRepository;
    private final ITransactionGenerateService transactionGenerateService;
    private final IProfileUpdateLevelService profileUpdateLevelService;
    private final SimulationProperties properties;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);
        List<Aspect> paidAspects = aspectRepository.findAllByDeletedAtIsNullOrderByTypeAscNameAsc().stream()
            .filter(a -> a.getDeletedAt() == null)
            .filter(a -> a.getType() == TypeAspect.REMERA || a.getType() == TypeAspect.SOMBRERO)
            .filter(a -> a.getPrice() != null && a.getPrice().doubleValue() > 0)
            .toList();

        if (paidAspects.isEmpty()) {
            log.warn("No hay aspectos de pago disponibles para simular compras");
            return;
        }

        Collections.shuffle(students, timeline.random());
        int buyerCount = Math.max(0, (int) Math.round(students.size() * properties.getStudentsBuyingAspectRate()));

        for (int i = 0; i < buyerCount && i < students.size(); i++) {
            Student student = students.get(i);
            Profile profile = student.getProfile();
            Aspect aspect = paidAspects.get(timeline.random().nextInt(paidAspects.size()));

            if (profile.getOwnedAspects().contains(aspect)) {
                continue;
            }

            double price = aspect.getPrice().doubleValue();
            if (student.getWallet().getBalance() < price) {
                continue;
            }

            transactionGenerateService.generate(
                TypeTransaction.COMPRA,
                price,
                "Compra simulada de aspecto",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                student.getWallet(),
                null,
                null,
                null,
                null,
                null,
                null
            );

            profile.getOwnedAspects().add(aspect);
            profileRepository.save(profile);
            profileUpdateLevelService.execute(profile, ValueXp.ASPECT_BOUGHT.getValue());
            collector.incrementAspectPurchases();
        }

        log.info("Compras de aspectos simuladas: {}", collector.getAspectPurchases());
    }
}
