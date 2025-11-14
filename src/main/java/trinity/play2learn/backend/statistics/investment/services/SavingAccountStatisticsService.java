package trinity.play2learn.backend.statistics.investment.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.statistics.investment.services.interfaces.ISavingAccountStatisticsService;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.statistics.investment.dtos.response.SavingAccountStatisticsResponseDto;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.statistics.investment.mappers.SavingAccountStatisticsMapper;


@Service
@AllArgsConstructor
public class SavingAccountStatisticsService implements ISavingAccountStatisticsService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final ISavingAccountRepository savingAccountRepository;
    
    @Override
    public SavingAccountStatisticsResponseDto execute(User user) {

        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        List<SavingAccount> savingAccounts = savingAccountRepository.findAllByWalletAndDeletedAtIsNull(wallet);

        if (savingAccounts.isEmpty()) {
            return SavingAccountStatisticsMapper.toDto(0.0, 0, 0.0);
        }

        System.out.println("savingAccounts: " + savingAccounts);

        Double totalInvested = 0.0;
        Double totalReward = 0.0;

        for (SavingAccount savingAccount : savingAccounts) {
            totalInvested += (savingAccount.getCurrentAmount() == null) ? 0.0 : savingAccount.getCurrentAmount();
            totalReward += (savingAccount.getAccumulatedInterest() == null) ? 0.0 : savingAccount.getAccumulatedInterest();
        }

        System.out.println("1");
        return SavingAccountStatisticsMapper.toDto(totalInvested, savingAccounts.size(), totalReward);
    }
}
