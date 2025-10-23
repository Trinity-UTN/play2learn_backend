package trinity.play2learn.backend.benefits.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentCountResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetLastPurchaseService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetPurchasesLeftByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitStudentCountService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitStudentCountService implements IBenefitStudentCountService {

    private final IStudentGetByEmailService studentGetByEmailService;
    private final IBenefitGetByStudentService benefitGetByStudentService;
    private final IBenefitGetLastPurchaseService benefitGetLastPurchaseService;
    private final IBenefitGetPurchasesLeftByStudentService benefitGetPurchasesLeftByStudentService;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    @Transactional(readOnly = true)
    public BenefitStudentCountResponseDto cu89CountByStudentState(User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        List<Benefit> benefits = benefitGetByStudentService.getByStudent(student);

        int used = benefitPurchaseRepository.countByStudentAndState(student, BenefitPurchaseState.USED);
        
        List<Benefit> available = new ArrayList<>();
        List<Benefit> purchased = new ArrayList<>();
        List<Benefit> use_requested = new ArrayList<>();
        List<Benefit> expired = new ArrayList<>();

        benefits.stream().forEach(benefit -> {

            Optional<BenefitPurchase> benefitPurchase = benefitGetLastPurchaseService.getLastPurchase(benefit, student);

            if (benefit.isExpired()) {

                expired.add(benefit);
                return;
            }

            if (!benefitPurchase.isEmpty() && benefitPurchase.get().isPurchased()) {

                purchased.add(benefit);
                return;
            }

            if (!benefitPurchase.isEmpty() && benefitPurchase.get().isUseRequested()) {

                use_requested.add(benefit);
                return;
            }

            if (benefit.getPurchasesLeft() == null || benefit.getPurchasesLeft() > 0) {

                Integer purchaseLeftByStudent = benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit, student);

                if (purchaseLeftByStudent == null || purchaseLeftByStudent > 0) {

                    available.add(benefit);

                }

            }

        });

        return BenefitMapper.tCountResponseDto(available.size(), purchased.size(), use_requested.size(), used, expired.size());
    }

}
