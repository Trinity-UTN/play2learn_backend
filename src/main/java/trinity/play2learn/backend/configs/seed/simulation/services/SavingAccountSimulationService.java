package trinity.play2learn.backend.configs.seed.simulation.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.utils.InvestmentSimulationConstants;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationDateValidator;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;

/**
 * Fase 7a: simula cajas de ahorro con tenencia de 7–15 días e interés diario 0.1%.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SavingAccountSimulationService {

    private final SimulationProperties properties;
    private final IStudentRepository studentRepository;
    private final ISavingAccountRepository savingAccountRepository;
    private final ITransactionGenerateService transactionGenerateService;
    private final IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);
        Collections.shuffle(students, timeline.random());

        int target = Math.max(0, (int) Math.round(students.size() * properties.getStudentsUsingSavingAccountRate()));
        int created = 0;
        int totalInterestDays = 0;
        int accountIndex = 1;

        for (int i = 0; i < target && i < students.size(); i++) {
            Student student = students.get(i);
            Wallet wallet = student.getWallet();

            double maxDeposit = wallet.getBalance() * properties.getMaxInvestmentAmountFactorOfBalance();
            if (maxDeposit < properties.getMinInvestmentAmount()) {
                continue;
            }

            double depositAmount = Math.max(
                properties.getMinInvestmentAmount(),
                Math.min(maxDeposit, wallet.getBalance() * 0.4)
            );
            if (wallet.getBalance() < depositAmount) {
                continue;
            }

            int holdingDays = timeline.randomIntBetween(
                properties.getSavingAccountHoldingDaysMin(),
                properties.getSavingAccountHoldingDaysMax()
            );

            LocalDate rangeEnd = timeline.getTo().toLocalDate().minusDays(holdingDays);
            LocalDate rangeStart = timeline.getFrom().toLocalDate();
            if (rangeStart.isAfter(rangeEnd)) {
                continue;
            }

            LocalDate startDate = timeline.randomLocalDateBetween(rangeStart, rangeEnd);
            LocalDate lastUpdate = startDate;

            SavingAccount account = savingAccountRepository.save(SavingAccount.builder()
                .name("Caja Simulada " + accountIndex++)
                .initialAmount(depositAmount)
                .currentAmount(depositAmount)
                .accumulatedInterest(0.0)
                .startDate(startDate)
                .lastUpdate(lastUpdate)
                .wallet(wallet)
                .build());

            transactionGenerateService.generate(
                TypeTransaction.INGRESO_CAJA_AHORRO,
                depositAmount,
                "Depósito simulado en caja de ahorro",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null, null, null, account
            );

            double currentAmount = depositAmount;
            double accumulated = 0.0;
            for (int day = 0; day < holdingDays; day++) {
                lastUpdate = lastUpdate.plusDays(1);
                double interest = InvestmentSimulationConstants.applyDailySavingInterest(currentAmount);
                currentAmount += interest;
                accumulated += interest;
                totalInterestDays++;
            }

            account.setCurrentAmount(currentAmount);
            account.setAccumulatedInterest(accumulated);
            account.setLastUpdate(lastUpdate);
            SimulationDateValidator.validateSavingAccountDates(account.getStartDate(), account.getLastUpdate());
            savingAccountRepository.save(account);

            transactionGenerateService.generate(
                TypeTransaction.RETIRO_CAJA_AHORRO,
                currentAmount,
                "Retiro simulado de caja de ahorro",
                TransactionActor.SISTEMA,
                TransactionActor.ESTUDIANTE,
                wallet,
                null, null, null, null, null, account
            );

            walletUpdateInvestedBalanceService.execute(wallet);
            collector.incrementSavingAccounts();
            collector.addSavingAccountInterestDays(holdingDays);
            created++;
        }

        log.info("Fase 7a: {} cajas de ahorro, {} días de interés simulados", created, totalInterestDays);
    }
}
