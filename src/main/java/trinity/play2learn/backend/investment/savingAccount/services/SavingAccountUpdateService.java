package trinity.play2learn.backend.investment.savingAccount.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountUpdateService;

@Service
@AllArgsConstructor
public class SavingAccountUpdateService implements ISavingAccountUpdateService {
    
    private final ISavingAccountRepository savingAccountRepository;
    
    @Override
    @Scheduled(cron = "0 35 1 * * *")
    @Transactional
    public void cu107updateSavingAccounts() {
        List<SavingAccount> savingAccounts = savingAccountRepository.findAllByDeletedAtIsNull();

        for (SavingAccount savingAccount : savingAccounts) {
            if (savingAccount.getLastUpdate().isBefore(LocalDate.now())) {

                // Incremento del 0.1% diario
                Double interest = savingAccount.getCurrentAmount() * 0.001;

                savingAccount.setAccumulatedInterest(savingAccount.getAccumulatedInterest() + interest);

                savingAccount.setCurrentAmount(savingAccount.getCurrentAmount() + interest);

                savingAccount.setLastUpdate(LocalDate.now());

                savingAccountRepository.save(savingAccount);
            }
        }
    }
    
}
