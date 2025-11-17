package trinity.play2learn.backend.benefits.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitAcceptUseService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitPurchaseGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitAcceptUseService implements IBenefitAcceptUseService {
    
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IBenefitPurchaseGetByIdService benefitPurchaseGetByIdService;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    @Transactional
    public BenefitPurchaseSimpleResponseDto cu85AcceptBenefitUse(User user, Long benefitPurchaseId) {
        
        BenefitPurchase benefitPurchase = benefitPurchaseGetByIdService.getById(benefitPurchaseId);

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        if (benefitPurchase.getBenefit().getDeletedAt() != null) {
            throw new ConflictException("No se puede aceptar la solicitud del beneficio ya que este ha sido eliminado");
        }

        if (!benefitPurchase.getBenefit().getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException("No se puede aceptar la solicitud de uso de este beneficio ya que no pertenece al docente.");
        }

        if (benefitPurchase.getBenefit().isExpired()) {
            throw new ConflictException("No se puede aceptar la solicitud de uso de este beneficio ya que ha expirado.");
        }

        if (!benefitPurchase.isUseRequested()) {
            throw new ConflictException("No se puede aceptar la solicitud de uso de este beneficio ya que no ha sido solicitada.");
        }

        benefitPurchase.setState(BenefitPurchaseState.USED);
        benefitPurchase.setUsedAt(LocalDateTime.now());

        return BenefitPurchaseMapper.toSimpleDto(benefitPurchaseRepository.save(benefitPurchase));
    }
}
