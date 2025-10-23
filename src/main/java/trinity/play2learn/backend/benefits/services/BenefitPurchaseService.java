package trinity.play2learn.backend.benefits.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectHasStudentService;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByIdService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitPurchaseService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitValidatePurchaseLimitService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitValidateIfPurchasedByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetPurchasesLeftByStudentService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitPurchaseService implements IBenefitPurchaseService {
    
    private final IStudentGetByEmailService studentGetByEmailService;
    private final ISubjectHasStudentService subjectHasStudentService;
    private final IBenefitGetByIdService benefitGetByIdService;
    private final IBenefitValidatePurchaseLimitService benefitValidatePurchaseLimitService;
    private final IBenefitGetPurchasesLeftByStudentService benefitGetPurchasesPerStudentService;
    private final IBenefitValidateIfPurchasedByStudentService benefitValidateIfPurchasedByStudentService;
    private final ITransactionGenerateService transactionGenerateService;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    @Transactional
    public BenefitPurchaseResponseDto cu75PurchaseBenefit(BenefitPurchaseRequestDto benefitRequestDto, User user) {
    
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Benefit benefit = benefitGetByIdService.getById(benefitRequestDto.getBenefitId());

        if (benefit.isExpired()) {
            throw new ConflictException("No se puede comprar este beneficio ya que ha expirado.");
        }
        
        //Valida si el estudiante esta inscrito en la materia del beneficio
        subjectHasStudentService.subjectHasStudent(benefit.getSubject(), student);

        //Valida límite de compras total
        benefitValidatePurchaseLimitService.validatePurchaseLimit(benefit);

        //Valida límite de compras por estudiante y devuelve el número de compras restantes
        //En caso de que el beneficio no tenga un límite de compras por estudiante, devuelve nulo
        Integer purchasesLeftByStudent = benefitGetPurchasesPerStudentService.getPurchasesLeftByStudent(benefit, student);
        if (purchasesLeftByStudent == 0) {
            throw new ConflictException("El estudiante ya ha alcanzado el límite de compras permitidas de este beneficio.");
        }

        if (benefit.getPurchaseLimitPerStudent() != null && benefit.getPurchaseLimitPerStudent() != 0) {
            purchasesLeftByStudent-= 1;    
        }

        //Valida si el estudiante ya ha comprado el beneficio y no lo ha usado aun.
        benefitValidateIfPurchasedByStudentService.validateIfPurchasedByStudent(benefit, student);

        //Genera la transacción de monedas
        transactionGenerateService.generate(
            TypeTransaction.COMPRA, 
            benefit.getCost().doubleValue(), 
            "Compra de beneficio", 
            TransactionActor.ESTUDIANTE, 
            TransactionActor.SISTEMA, 
            student.getWallet(), 
            null, 
            null,
            benefit,
            null
        );

        //Decrementa el número de compras restantes si el beneficio tiene un límite
        benefit.decrementPurchasesLeft();

        return BenefitPurchaseMapper.toDto(benefitPurchaseRepository.save(BenefitPurchaseMapper.toModel(benefit, student)), purchasesLeftByStudent);
    }
}