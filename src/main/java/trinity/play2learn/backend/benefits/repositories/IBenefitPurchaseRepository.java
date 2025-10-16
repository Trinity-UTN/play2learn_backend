package trinity.play2learn.backend.benefits.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;

public interface IBenefitPurchaseRepository extends CrudRepository<BenefitPurchase, Long> {
    
    List<BenefitPurchase> findByBenefit(Benefit benefit);

    List<BenefitPurchase> findByBenefitAndStudent(Benefit benefit, Student student);
}
