package trinity.play2learn.backend.investment.savingAccount.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountDepositRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.mappers.SavingAccountMapper;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountDepositService;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountFindByIdService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class SavingAccountDepositService implements ISavingAccountDepositService {
    
    private final IStudentGetByEmailService studentGetByEmailService;

    private final ISavingAccountFindByIdService savingAccountFindByIdService;

    private final ISavingAccountRepository savingAccountRepository;

    private final ITransactionGenerateService transactionGenerateService;

    private final IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;
    
    @Override
    @Transactional
    public SavingAccountResponseDto cu103depositSavingAccount(SavingAccountDepositRequestDto dto, User user) {
        
        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        SavingAccount savingAccount = savingAccountFindByIdService.execute(dto.getId());

        if (!savingAccount.getWallet().equals(wallet)){
            throw new ConflictException("La cuenta de ahorro no pertenece al estudiante");
        }

        if (wallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new BadRequestException("No hay saldo suficiente en la wallet");
        }

        savingAccount.setCurrentAmount(
            savingAccount.getCurrentAmount()+dto.getAmount()
        );

        savingAccountRepository.save(savingAccount);

        transactionGenerateService.generate(
            TypeTransaction.INGRESO_CAJA_AHORRO, 
            dto.getAmount(), 
            "Deposito en caja de ahorro", 
            TransactionActor.ESTUDIANTE, 
            TransactionActor.SISTEMA, 
            wallet, 
            null, 
            null,
            null,
            null,
            null,
            savingAccount
        );

        walletUpdateInvestedBalanceService.execute(wallet);

        return SavingAccountMapper.toDto(savingAccount);
    }
    
}
