package trinity.play2learn.backend.benefits.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitDeleteService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitDeleteService implements IBenefitDeleteService {
    
    private final IBenefitRepository benefitRepository;
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IBenefitGetByIdService benefitGetByIdService;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;
    private final ITransactionGenerateService transactionGenerateService;

    @Override
    @Transactional
    public void cu94DeleteBenefit(User user, Long id) {
        
        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());
        
        Benefit benefit = benefitGetByIdService.getById(id);

        if (!benefit.getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException("No es posible eliminar este beneficio ya que no pertenece al docente");
        }

        if (!benefit.isExpired()) { //Si esta expirado salta la logica de reembolso
            List<BenefitPurchase> benefitPurchases = benefitPurchaseRepository.findByBenefit(benefit);

            for (BenefitPurchase benefitPurchase : benefitPurchases) {
                if (benefitPurchase.getState() != BenefitPurchaseState.USED) {

                    //Si un estudiante tiene el beneficio comprado o solicito su uso, se le reemboalza lo que pago
                    transactionGenerateService.generate(
                        TypeTransaction.REEMBOLSO, 
                        (double) benefit.getCost(), 
                        "Reembolso de beneficio", 
                        TransactionActor.SISTEMA, 
                        TransactionActor.ESTUDIANTE, 
                        benefitPurchase.getStudent().getWallet(), 
                        null, 
                        null, 
                        benefit, 
                        null);
                }
            }


        }
        benefit.setDeletedAt(LocalDateTime.now());

        benefitRepository.save(benefit);
    }
    
    
}
