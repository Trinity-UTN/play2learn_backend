package trinity.play2learn.backend.statistics.investment.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositFindAllByStateAndWalletService;
import trinity.play2learn.backend.statistics.investment.dtos.response.FixedTermDepositStatisticsResponseDto;
import trinity.play2learn.backend.statistics.investment.mappers.FixedTermDepositStatisticsMapper;
import trinity.play2learn.backend.statistics.investment.services.interfaces.IFixedTermDepositStatisticsService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class FixedTermDepositStatisticsService implements IFixedTermDepositStatisticsService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IFixedTermDepositFindAllByStateAndWalletService fixedTermDepositFindAllByStateAndWalletService;
    
    @Override
    public FixedTermDepositStatisticsResponseDto execute(User user) {

        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        List<FixedTermDeposit> fixedTermDeposits = fixedTermDepositFindAllByStateAndWalletService.execute(
            FixedTermState.IN_PROGRESS,
            wallet
        );

        Double totalInvested = fixedTermDeposits.stream()
            .map(FixedTermDeposit::getAmountInvested)
            .reduce(0.0, Double::sum);

        Double totalReward = fixedTermDeposits.stream()
            .map(FixedTermDeposit::getAmountReward)
            .reduce(0.0, Double::sum);

        Integer quantityInProgress = fixedTermDeposits.size();
        

        return FixedTermDepositStatisticsMapper.toDto(
            totalInvested, 
            totalReward, 
            quantityInProgress
        );
    }
    
}
