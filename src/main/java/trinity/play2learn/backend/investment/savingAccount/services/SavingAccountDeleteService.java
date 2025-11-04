package trinity.play2learn.backend.investment.savingAccount.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountDeleteService;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountFindByIdService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class SavingAccountDeleteService implements ISavingAccountDeleteService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final ISavingAccountFindByIdService savingAccountFindByIdService;

    private final ISavingAccountRepository savingAccountRepository;

    private final ITransactionGenerateService transactionGenerateService;
    
    @Override
    @Transactional
    public void cu105deleteSavingAccount(Long id, User user) {
        
        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();
        
        SavingAccount savingAccount = savingAccountFindByIdService.execute(id);

        if (!savingAccount.getWallet().equals(wallet)){
            throw new ConflictException("La cuenta de ahorro no pertenece al estudiante");
        }

        savingAccount.setDeletedAt(LocalDateTime.now());

        if (Double.compare(savingAccount.getCurrentAmount(), 0.0) > 0) {
            transactionGenerateService.generate(
                TypeTransaction.RETIRO_CAJA_AHORRO, 
                savingAccount.getCurrentAmount(), 
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
        }

        savingAccount.setCurrentAmount(0.0);

        savingAccountRepository.save(savingAccount);

    }
    
}
