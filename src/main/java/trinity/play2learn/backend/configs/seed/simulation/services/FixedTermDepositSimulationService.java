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
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationDateValidator;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.mappers.FixedTermDepositMapper;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositCalculateInterestService;

/**
 * Fase 7b: simula plazos fijos con plazos 7/15/30 días y vencimientos coherentes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FixedTermDepositSimulationService {

    private final SimulationProperties properties;
    private final IStudentRepository studentRepository;
    private final IFixedTermDepositRepository fixedTermDepositRepository;
    private final IFixedTermDepositCalculateInterestService fixedTermDepositCalculateInterestService;
    private final ITransactionGenerateService transactionGenerateService;
    private final IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);
        Collections.shuffle(students, timeline.random());

        int target = Math.max(0, (int) Math.round(students.size() * properties.getStudentsUsingFixedTermRate()));
        LocalDate today = LocalDate.now();
        int created = 0;
        int finished = 0;
        int pending = 0;
        int semanal = 0;
        int quincenal = 0;
        int mensual = 0;

        FixedTermDays[] allTerms = FixedTermDays.values();

        for (int i = 0; i < target && i < students.size(); i++) {
            Student student = students.get(i);
            Wallet wallet = student.getWallet();

            FixedTermDays termDays = allTerms[timeline.random().nextInt(allTerms.length)];
            boolean shouldFinish = timeline.random().nextDouble() < properties.getFixedTermFinishedBeforeTodayRate();

            LocalDate startDate = resolveStartDate(timeline, termDays, shouldFinish, today);
            if (startDate == null) {
                continue;
            }

            LocalDate endDate = SimulationDateValidator.computeFixedTermEndDate(startDate, termDays);
            FixedTermState state = endDate.isAfter(today) ? FixedTermState.IN_PROGRESS : FixedTermState.FINISHED;

            double maxInvest = wallet.getBalance() * properties.getMaxInvestmentAmountFactorOfBalance();
            if (maxInvest < properties.getMinInvestmentAmount()) {
                continue;
            }
            double amountInvested = Math.max(
                properties.getMinInvestmentAmount(),
                Math.min(maxInvest, wallet.getBalance() * 0.35)
            );
            if (wallet.getBalance() < amountInvested) {
                continue;
            }

            Double interest = fixedTermDepositCalculateInterestService.execute(amountInvested, termDays);
            double amountReward = amountInvested + interest;

            FixedTermDepositRegisterRequestDto dto = FixedTermDepositRegisterRequestDto.builder()
                .amountInvested(amountInvested)
                .fixedTermDays(termDays)
                .build();

            FixedTermDeposit deposit = fixedTermDepositRepository.save(FixedTermDepositMapper.toEntity(
                dto, amountReward, startDate, endDate, wallet, state
            ));

            SimulationDateValidator.validateFixedTermDates(startDate, endDate, termDays, state, today);

            transactionGenerateService.generate(
                TypeTransaction.PLAZO_FIJO,
                amountInvested,
                "Inversión simulada en plazo fijo",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null, null, deposit, null
            );

            if (state == FixedTermState.FINISHED) {
                transactionGenerateService.generate(
                    TypeTransaction.PLAZO_FIJO,
                    amountReward,
                    "Retorno simulado de plazo fijo",
                    TransactionActor.SISTEMA,
                    TransactionActor.ESTUDIANTE,
                    wallet,
                    null, null, null, null, deposit, null
                );
                finished++;
            } else {
                pending++;
            }

            walletUpdateInvestedBalanceService.execute(wallet);
            collector.incrementFixedTermDeposits();
            if (state == FixedTermState.FINISHED) {
                collector.incrementFixedTermDepositsFinished();
            } else {
                collector.incrementFixedTermDepositsPending();
            }

            switch (termDays) {
                case SEMANAL -> semanal++;
                case QUINCENAL -> quincenal++;
                case MENSUAL -> mensual++;
            }
            created++;
        }

        log.info(
            "Fase 7b: {} plazos (7d={}, 15d={}, 30d={}), finalizados={}, pendientes={}",
            created, semanal, quincenal, mensual, finished, pending
        );
    }

    private LocalDate resolveStartDate(
        SimulationTimeline timeline,
        FixedTermDays termDays,
        boolean shouldFinish,
        LocalDate today
    ) {
        int termLength = termDays.getValor();
        LocalDate rangeStart = timeline.getFrom().toLocalDate();
        LocalDate rangeEnd = timeline.getTo().toLocalDate();

        if (shouldFinish) {
            LocalDate latestStart = today.minusDays(termLength);
            if (latestStart.isBefore(rangeStart)) {
                return null;
            }
            LocalDate effectiveEnd = rangeEnd.isBefore(latestStart) ? rangeEnd : latestStart;
            if (rangeStart.isAfter(effectiveEnd)) {
                return null;
            }
            return timeline.randomLocalDateBetween(rangeStart, effectiveEnd);
        }

        LocalDate earliestStart = today.minusDays(termLength - 1);
        LocalDate effectiveStart = rangeStart.isAfter(earliestStart) ? rangeStart : earliestStart;
        if (effectiveStart.isAfter(rangeEnd)) {
            return null;
        }
        return timeline.randomLocalDateBetween(effectiveStart, rangeEnd);
    }
}
