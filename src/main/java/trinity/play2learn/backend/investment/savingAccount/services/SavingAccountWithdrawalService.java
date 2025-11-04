package trinity.play2learn.backend.investment.savingAccount.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountWithdrawalRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.mappers.SavingAccountMapper;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountFindByIdService;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountWithdrawalService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class SavingAccountWithdrawalService implements ISavingAccountWithdrawalService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final ISavingAccountFindByIdService savingAccountFindByIdService;

    private final ISavingAccountRepository savingAccountRepository;

    private final ITransactionGenerateService transactionGenerateService;
    
    @Override
    public SavingAccountResponseDto cu104withdrawalSavingAccount(SavingAccountWithdrawalRequestDto dto, User user) {
        
        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        SavingAccount savingAccount = savingAccountFindByIdService.execute(dto.getId());

        if (!savingAccount.getWallet().equals(wallet)){
            throw new ConflictException("La cuenta de ahorro no pertenece al estudiante");
        }

        if (savingAccount.getCurrentAmount().compareTo(dto.getAmount()) < 0) {
            throw new BadRequestException("No hay saldo suficiente en la caja de ahorro");
        }

        transactionGenerateService.generate(
            TypeTransaction.RETIRO_CAJA_AHORRO, 
            dto.getAmount(), 
            "Retiro de caja de ahorro", 
            TransactionActor.SISTEMA, 
            TransactionActor.ESTUDIANTE, 
            wallet, 
            null, 
            null,
            null,
            null,
            null,
            savingAccount
        );

        savingAccount.setCurrentAmount(savingAccount.getCurrentAmount()-dto.getAmount());

        savingAccountRepository.save(savingAccount);

        return SavingAccountMapper.toDto(savingAccount);
    }
    
}
