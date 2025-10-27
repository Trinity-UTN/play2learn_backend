package trinity.play2learn.backend.benefits.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByIdService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListPurchasesService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitListPurchasesService implements IBenefitListPurchasesService {
    
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IBenefitGetByIdService benefitGetByIdService;

    private final IBenefitPurchaseRepository benefitPurchaseRepository;
    @Override
    @Transactional(readOnly = true)
    public List<BenefitPurchaseSimpleResponseDto> cu98ListPurchasesByBenefitId(User user, Long benefitId) {
        
        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Benefit benefit = benefitGetByIdService.getById(benefitId);

        if (!benefit.getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException("No se puede obtener las compras de este beneficio ya que no pertenece al docente.");
        }

        List<BenefitPurchase> benefitPurchases = benefitPurchaseRepository.findAllByBenefit(benefit);

        return BenefitPurchaseMapper.toSimpleDtoList(benefitPurchases);
    }
    

}
