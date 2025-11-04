package trinity.play2learn.backend.investment.savingAccount.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountRegisterRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.mappers.SavingAccountMapper;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountExistsByNameAndWalletService;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountRegisterService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class SavingAccountRegisterService implements ISavingAccountRegisterService {
    
    private final IStudentGetByEmailService studentGetByEmailService;

    private final ISavingAccountExistsByNameAndWalletService savingAccountExistsByNameAndWalletService;

    private final ISavingAccountRepository savingAccountRepository;

    private final ITransactionGenerateService transactionGenerateService;

    private final IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;
    
    @Override
    @Transactional
    public SavingAccountResponseDto cu102registerSavingAccount(SavingAccountRegisterRequestDto dto, User user) {
        
        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        if (wallet.getBalance().compareTo(dto.getInitialAmount()) < 0) {
            throw new BadRequestException("No hay saldo suficiente en la wallet");
        }

        if (savingAccountExistsByNameAndWalletService.execute(dto.getName(), wallet)) {
            throw new BadRequestException("Ya existe una cuenta de ahorro con ese nombre");
        }

        SavingAccount savingAccount = savingAccountRepository.save(SavingAccountMapper.toModel(dto, wallet));

        transactionGenerateService.generate(
            TypeTransaction.INGRESO_CAJA_AHORRO, 
            savingAccount.getInitialAmount(), 
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
