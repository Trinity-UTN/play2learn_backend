package trinity.play2learn.backend.benefits.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;

public interface IBenefitPurchaseRepository extends CrudRepository<BenefitPurchase, Long> {

    List<BenefitPurchase> findByBenefit(Benefit benefit);

    List<BenefitPurchase> findByBenefitAndStudent(Benefit benefit, Student student);

    Optional<BenefitPurchase> findTopByBenefitAndStudentOrderByPurchasedAtDesc(Benefit benefit, Student student);

    // Trae todos las solicitudes de uso de un beneficio que no este expirado ni
    // eliminado
    List<BenefitPurchase> findAllByBenefitAndState(Benefit benefit, BenefitPurchaseState state);

    Optional<BenefitPurchase> findByIdAndDeletedAtIsNull(Long id);
}
