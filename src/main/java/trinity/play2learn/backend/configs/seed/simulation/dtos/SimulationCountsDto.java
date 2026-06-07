package trinity.play2learn.backend.configs.seed.simulation.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationCountsDto {

    private int activities;
    private int attempts;
    private int approved;
    private int disapproved;
    private int benefits;
    private int benefitPurchases;
    private int benefitUseRequests;
    private int benefitUsesAccepted;
    private int aspectPurchases;

    /** Acciones creadas en fase 6. */
    private int stocksCreated;
    /** Cajas de ahorro abiertas en fase 7a. */
    private int savingAccounts;
    /** Días totales de interés simulados en cajas. */
    private int savingAccountInterestDays;
    /** Plazos fijos registrados. */
    private int fixedTermDeposits;
    /** Plazos con endDate <= hoy (FINISHED). */
    private int fixedTermDepositsFinished;
    /** Plazos con endDate > hoy (IN_PROGRESS). */
    private int fixedTermDepositsPending;
    /** Órdenes de compra ejecutadas. */
    private int stockBuys;
    /** Órdenes de venta ejecutadas. */
    private int stockSells;
    /** Registros de StockHistory generados (incluye evolución diaria). */
    private int stockHistoryRecords;
}
