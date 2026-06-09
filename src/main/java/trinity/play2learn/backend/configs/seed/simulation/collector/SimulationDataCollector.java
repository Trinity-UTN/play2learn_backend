package trinity.play2learn.backend.configs.seed.simulation.collector;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationCountsDto;
import trinity.play2learn.backend.investment.stock.models.Stock;

/**
 * Acumula entidades y contadores durante la simulación.
 */
@Getter
public class SimulationDataCollector {

    private final List<Long> activityIds = new ArrayList<>();
    private final List<Benefit> benefits = new ArrayList<>();
    private final List<Stock> stocks = new ArrayList<>();

    private int attempts;
    private int approved;
    private int disapproved;
    private int benefitPurchases;
    private int benefitUseRequests;
    private int benefitUsesAccepted;
    private int aspectPurchases;

    private int stocksCreated;
    private int savingAccounts;
    private int savingAccountInterestDays;
    private int fixedTermDeposits;
    private int fixedTermDepositsFinished;
    private int fixedTermDepositsPending;
    private int stockBuys;
    private int stockSells;
    private int stockHistoryRecords;

    public void addActivityId(Long activityId) {
        activityIds.add(activityId);
    }

    public void addBenefit(Benefit benefit) {
        benefits.add(benefit);
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public void incrementAttempts() {
        attempts++;
    }

    public void incrementApproved() {
        approved++;
    }

    public void incrementDisapproved() {
        disapproved++;
    }

    public void incrementBenefitPurchases() {
        benefitPurchases++;
    }

    public void incrementBenefitUseRequests() {
        benefitUseRequests++;
    }

    public void incrementBenefitUsesAccepted() {
        benefitUsesAccepted++;
    }

    public void incrementAspectPurchases() {
        aspectPurchases++;
    }

    public void incrementStocksCreated() {
        stocksCreated++;
    }

    public void incrementSavingAccounts() {
        savingAccounts++;
    }

    public void addSavingAccountInterestDays(int days) {
        savingAccountInterestDays += days;
    }

    public void incrementFixedTermDeposits() {
        fixedTermDeposits++;
    }

    public void incrementFixedTermDepositsFinished() {
        fixedTermDepositsFinished++;
    }

    public void incrementFixedTermDepositsPending() {
        fixedTermDepositsPending++;
    }

    public void incrementStockBuys() {
        stockBuys++;
    }

    public void incrementStockSells() {
        stockSells++;
    }

    public void addStockHistoryRecords(int count) {
        stockHistoryRecords += count;
    }

    public SimulationCountsDto toCounts() {
        return SimulationCountsDto.builder()
            .activities(activityIds.size())
            .attempts(attempts)
            .approved(approved)
            .disapproved(disapproved)
            .benefits(benefits.size())
            .benefitPurchases(benefitPurchases)
            .benefitUseRequests(benefitUseRequests)
            .benefitUsesAccepted(benefitUsesAccepted)
            .aspectPurchases(aspectPurchases)
            .stocksCreated(stocksCreated)
            .savingAccounts(savingAccounts)
            .savingAccountInterestDays(savingAccountInterestDays)
            .fixedTermDeposits(fixedTermDeposits)
            .fixedTermDepositsFinished(fixedTermDepositsFinished)
            .fixedTermDepositsPending(fixedTermDepositsPending)
            .stockBuys(stockBuys)
            .stockSells(stockSells)
            .stockHistoryRecords(stockHistoryRecords)
            .build();
    }
}
