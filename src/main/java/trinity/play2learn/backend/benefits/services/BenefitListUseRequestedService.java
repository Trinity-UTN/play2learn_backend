package trinity.play2learn.backend.benefits.services;

import java.util.ArrayList;
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
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUseRequestedService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitListUseRequestedService implements IBenefitListUseRequestedService {
    
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IBenefitRepository benefitRepository;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BenefitPurchaseSimpleResponseDto> cu82ListUseRequestedByTeacher(User user) {
        
        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        //Trae todos los beneficios del docente
        List<Benefit> teacherBenefits = benefitRepository.findAllBySubjectTeacherAndDeletedAtIsNull(teacher);
        
        List<BenefitPurchaseSimpleResponseDto> benefitDtos = new ArrayList<>();
 
        for (Benefit benefit : teacherBenefits) {
            
            //Si el beneficio esta eliminado o expirado, lo ignora
            if (benefit.getDeletedAt() != null || benefit.isExpired()) {
                continue;
            }

            //Trae todas las solicitudes de uso de un beneficio
            List<BenefitPurchase> useRequest= benefitPurchaseRepository.findAllByBenefitAndState(benefit, BenefitPurchaseState.USE_REQUESTED);
            
            benefitDtos.addAll(BenefitPurchaseMapper.toSimpleDtoList(useRequest, BenefitPurchaseState.USE_REQUESTED));
        }

        return benefitDtos;

    }
    
}
