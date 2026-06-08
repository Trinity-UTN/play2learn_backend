package trinity.play2learn.backend.configs.seed.simulation.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;

class SimulationDateValidatorInvestmentTest {

    private static final LocalDate TODAY = LocalDate.of(2025, 6, 7);

    @Nested
    @DisplayName("validateSavingAccountDates (R9)")
    class SavingAccountDates {

        @Test
        @DisplayName("Given lastUpdate before startDate When validating Then throws")
        void lastUpdateBeforeStart_throws() {
            LocalDate start = TODAY;
            LocalDate lastUpdate = start.minusDays(1);
            assertThatThrownBy(() -> SimulationDateValidator.validateSavingAccountDates(start, lastUpdate))
                .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("Given coherent dates When validating Then passes")
        void coherentDates_passes() {
            SimulationDateValidator.validateSavingAccountDates(TODAY, TODAY.plusDays(7));
        }
    }

    @Nested
    @DisplayName("validateFixedTermDates (R10/R11)")
    class FixedTermDates {

        @Test
        @DisplayName("Given wrong endDate When validating Then throws")
        void wrongEndDate_throws() {
            LocalDate start = TODAY.minusDays(30);
            LocalDate wrongEnd = start.plusDays(10);
            assertThatThrownBy(() ->
                SimulationDateValidator.validateFixedTermDates(
                    start, wrongEnd, FixedTermDays.MENSUAL, FixedTermState.FINISHED, TODAY
                )
            ).isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("Given FINISHED with future endDate When validating Then throws")
        void finishedInFuture_throws() {
            LocalDate start = TODAY.minusDays(5);
            LocalDate end = TODAY.plusDays(2);
            assertThatThrownBy(() ->
                SimulationDateValidator.validateFixedTermDates(
                    start, end, FixedTermDays.SEMANAL, FixedTermState.FINISHED, TODAY
                )
            ).isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("computeFixedTermEndDate returns start + term days")
        void computeEndDate_correct() {
            LocalDate start = TODAY;
            LocalDate end = SimulationDateValidator.computeFixedTermEndDate(start, FixedTermDays.QUINCENAL);
            assertThat(end).isEqualTo(start.plusDays(15));
        }
    }

    @Nested
    @DisplayName("validateOrderDate (R12)")
    class OrderDate {

        @Test
        @DisplayName("Given order before stock creation When validating Then throws")
        void orderBeforeStock_throws() {
            LocalDateTime stockAt = LocalDateTime.of(2025, 1, 10, 8, 0);
            LocalDateTime orderAt = LocalDateTime.of(2025, 1, 9, 8, 0);
            assertThatThrownBy(() -> SimulationDateValidator.validateOrderDate(orderAt, stockAt))
                .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    @DisplayName("validateStockHistorySequence (R13)")
    class StockHistorySequence {

        @Test
        @DisplayName("Given non-increasing history When validating Then throws")
        void nonIncreasing_throws() {
            LocalDateTime prev = LocalDateTime.of(2025, 1, 2, 1, 0);
            LocalDateTime current = LocalDateTime.of(2025, 1, 1, 1, 0);
            assertThatThrownBy(() -> SimulationDateValidator.validateStockHistorySequence(prev, current))
                .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    @DisplayName("InvestmentSimulationConstants")
    class Constants {

        @Test
        @DisplayName("applyDailySavingInterest applies 0.1%")
        void dailyInterest_correct() {
            double interest = InvestmentSimulationConstants.applyDailySavingInterest(1000.0);
            assertThat(interest).isEqualTo(1.0);
        }

        @Test
        @DisplayName("accumulated interest for 7 and 15 days with seed formula")
        void accumulatedInterest_7and15days() {
            assertThat(accumulateInterest(1000.0, 7)).isGreaterThan(7.0);
            assertThat(accumulateInterest(1000.0, 15)).isGreaterThan(accumulateInterest(1000.0, 7));
        }

        private double accumulateInterest(double principal, int days) {
            double current = principal;
            double total = 0.0;
            for (int i = 0; i < days; i++) {
                double interest = InvestmentSimulationConstants.applyDailySavingInterest(current);
                current += interest;
                total += interest;
            }
            return total;
        }

        @Test
        @DisplayName("clampStockPrice respects floor and ceiling")
        void clampStockPrice_bounds() {
            assertThat(InvestmentSimulationConstants.clampStockPrice(5.0, 100.0)).isEqualTo(10.0);
            assertThat(InvestmentSimulationConstants.clampStockPrice(500.0, 100.0)).isEqualTo(250.0);
        }
    }
}
