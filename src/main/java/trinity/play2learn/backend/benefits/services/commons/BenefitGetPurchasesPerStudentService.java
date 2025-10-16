package trinity.play2learn.backend.benefits.services.commons;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetPurchasesPerStudentService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class BenefitGetPurchasesPerStudentService implements IBenefitGetPurchasesPerStudentService{
    
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    //Valida límite de compras por estudiante Y devuelve el número de compras restantes
    //Si el beneficio no tiene un límite de compras por estudiante, no se valida y devuelve nulo
    @Override
    public Integer getPurchasesLeftByStudent(Benefit benefit, Student student) {
        
        //Si el beneficio no tiene un límite de compras por estudiante, no se valida
        if (benefit.getPurchaseLimitPerStudent() == null || benefit.getPurchaseLimitPerStudent() == 0) {
            return null;
        }

        List<BenefitPurchase> benefitPurchasesByStudent = benefitPurchaseRepository.findByBenefitAndStudent(benefit, student);
        
        if (benefitPurchasesByStudent.size() >= benefit.getPurchaseLimitPerStudent()) {
            throw new ConflictException("El estudiante ya ha alcanzado el límite de compras permitidas de este beneficio.");
        }

        return benefit.getPurchaseLimitPerStudent() - benefitPurchasesByStudent.size();
    }
}
