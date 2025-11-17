package trinity.play2learn.backend.benefits.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitValidateIfPurchasedByStudentService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class BenefitValidateIfPurchasedByStudentService implements IBenefitValidateIfPurchasedByStudentService {
    
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    public void validateIfPurchasedByStudent(Benefit benefit, Student student) {
        
        List<BenefitPurchase> benefitPurchasesByStudent = benefitPurchaseRepository.findByBenefitAndStudent(benefit, student);

        benefitPurchasesByStudent.forEach(purchase -> {
            if (!purchase.isUsed()) {
                throw new ConflictException("El estudiante debe usar este beneficio antes de poder volver a comprarlo.");
            }
        });
    }
    
    
}
