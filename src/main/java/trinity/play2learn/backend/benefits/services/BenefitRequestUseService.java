package trinity.play2learn.backend.benefits.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectHasStudentService;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByIdService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetLastPurchaseService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitRequestUseService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitRequestUseService implements IBenefitRequestUseService {
    
    private final IStudentGetByEmailService studentGetByEmailService;
    private final IBenefitGetByIdService benefitGetByIdService;
    private final ISubjectHasStudentService subjectHasStudentService;
    private final IBenefitGetLastPurchaseService benefitGetLastPurchaseService;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    @Transactional
    public BenefitPurchaseSimpleResponseDto cu81RequestBenefitUse(User user,
            Long benefitId) {
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Benefit benefit = benefitGetByIdService.getById(benefitId);

        if (benefit.isExpired()) {
            throw new ConflictException("El beneficio ha expirado.");
        }

        //Valida si el estudiante esta inscrito en la materia del beneficio
        subjectHasStudentService.subjectHasStudent(benefit.getSubject(), student);
        
        Optional<BenefitPurchase> optionalLastBenefitPurchase = benefitGetLastPurchaseService.getLastPurchase(benefit, student);
    
        if (optionalLastBenefitPurchase.isEmpty() || !optionalLastBenefitPurchase.get().isPurchased()) {
            throw new ConflictException("El estudiante debe comprar el beneficio para solicitar su uso.");
        }

        BenefitPurchase lastBenefitPurchase = optionalLastBenefitPurchase.get();

        lastBenefitPurchase.setState(BenefitPurchaseState.USE_REQUESTED);

        return BenefitPurchaseMapper.toSimpleDto(benefitPurchaseRepository.save(lastBenefitPurchase));
    } 
}
