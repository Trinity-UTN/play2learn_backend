package trinity.play2learn.backend.investment.fixedTermDeposit.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.mappers.FixedTermDepositMapper;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositCalculateInterestService;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositRegisterService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class FixedTermDepositRegisterService implements IFixedTermDepositRegisterService {
    
    private final IStudentGetByEmailService studentGetByEmailService;

    private final IFixedTermDepositCalculateInterestService fixedTermDepositCalculateInterestService;
    
    private final IFixedTermDepositRepository fixedTermDepositRepository;

    private final ITransactionGenerateService transactionGenerateService;

    @Override
    @Transactional
    public FixedTermDepositResponseDto cu92registerFixedTermDeposit(
            FixedTermDepositRegisterRequestDto fixedTermDepositRegisterRequestDto,
            User user
        ) {
        
        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        if (wallet.getBalance().compareTo(fixedTermDepositRegisterRequestDto.getAmountInvested()) < 0) {
            throw new UnsupportedOperationException("No hay saldo suficiente en la wallet");
        }

        Double interest = fixedTermDepositCalculateInterestService.execute(fixedTermDepositRegisterRequestDto.getAmountInvested(), fixedTermDepositRegisterRequestDto.getFixedTermDays());

        Double amountReward = fixedTermDepositRegisterRequestDto.getAmountInvested() + interest;

        LocalDate endDate = LocalDate.now().plusDays(fixedTermDepositRegisterRequestDto.getFixedTermDays().getValor());

        FixedTermDeposit fixedTermDeposit = fixedTermDepositRepository.save(FixedTermDepositMapper.toEntity(
                fixedTermDepositRegisterRequestDto,
                amountReward,
                LocalDate.now(),
                endDate,
                wallet,
                FixedTermState.IN_PROGRESS
            )
        );

        transactionGenerateService.generate(
            TypeTransaction.PLAZO_FIJO, 
            fixedTermDepositRegisterRequestDto.getAmountInvested(), 
            "Inversion en plazo fijo.", 
            TransactionActor.ESTUDIANTE, 
            TransactionActor.SISTEMA, 
            wallet, 
            null, 
            null,
            null,
            null,
            fixedTermDeposit
        );

        return FixedTermDepositMapper.toDto(fixedTermDeposit);
        
    }
    
}
