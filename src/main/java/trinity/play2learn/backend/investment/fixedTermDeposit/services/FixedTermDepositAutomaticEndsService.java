package trinity.play2learn.backend.investment.fixedTermDeposit.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositAutomaticEndsService;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositFindAllByStateService;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCreateSingleWithTitleService;

@Service
@AllArgsConstructor
public class FixedTermDepositAutomaticEndsService implements IFixedTermDepositAutomaticEndsService {
    
    private final IFixedTermDepositFindAllByStateService fixedTermDepositFindAllByStateService;
    private final IFixedTermDepositRepository fixedTermDepositRepository;
    private final ITransactionGenerateService transactionGenerateService;
    private final IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;
    private final INotificationCreateSingleWithTitleService notificationCreateSingleWithTitleService;

    @Override
    @Scheduled(cron = "0 30 1 * * *")
    @Transactional
    public void cu95fixedTermDepositAutomaticEnds() {
        List<FixedTermDeposit> fixedTerms = fixedTermDepositFindAllByStateService.execute(FixedTermState.IN_PROGRESS);

        for (FixedTermDeposit fixedTermDeposit : fixedTerms) {
            if (fixedTermDeposit.getEndDate().isAfter(LocalDate.now())) {
                continue;
            }

            fixedTermDeposit.setFixedTermState(FixedTermState.FINISHED);

            fixedTermDepositRepository.save (
                fixedTermDeposit
            );

            transactionGenerateService.generate(
                TypeTransaction.PLAZO_FIJO, 
                fixedTermDeposit.getAmountReward(), 
                "Inversion en plazo fijo, retorno.", 
                TransactionActor.SISTEMA, 
                TransactionActor.ESTUDIANTE, 
                fixedTermDeposit.getWallet(), 
                null, 
                null,
                null,
                null,
                fixedTermDeposit,
                null
            );

            walletUpdateInvestedBalanceService.execute(fixedTermDeposit.getWallet());

            //Genera la notificacion al estudiante
            notificationCreateSingleWithTitleService.createSingleNotificationWithTitle(
                fixedTermDeposit.getWallet().getStudent().getUser(),
                NotificationType.FIXED_TERM_DEPOSIT_ENDED,
                null
            );
        }

    }
    
}
