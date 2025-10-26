package trinity.play2learn.backend.benefits.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchasedUsedResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUsedByStudentService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitListUsedByStudentService implements IBenefitListUsedByStudentService {
    
    private final IBenefitPurchaseRepository benefitPurchaseRepository;
    private final IStudentGetByEmailService studentGetByEmailService;

    @Override
    @Transactional(readOnly = true)
    public List<BenefitPurchasedUsedResponseDto> cu93ListUsedByStudent(User user) {
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());
        
        return BenefitPurchaseMapper.toUsedDtoList(benefitPurchaseRepository.findAllByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED));
    }
    
    
}
