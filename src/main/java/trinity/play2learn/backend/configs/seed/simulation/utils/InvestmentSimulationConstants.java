package trinity.play2learn.backend.configs.seed.simulation.utils;

/**
 * Constantes compartidas de la simulación de inversiones.
 *
 * @see docs/seed/simulation-investment-architecture.md
 */
public final class InvestmentSimulationConstants {

    public static final double DAILY_SAVING_INTEREST_RATE = 0.001;
    public static final double STOCK_PRICE_FLOOR = 10.0;
    public static final double STOCK_PRICE_CEILING_MULTIPLIER = 2.5;
    public static final double STOCK_INITIAL_PRICE_ABSOLUTE_MAX = 150.0;

    private InvestmentSimulationConstants() {
    }

    public static double clampStockPrice(double price, double initialPrice) {
        double ceiling = initialPrice * STOCK_PRICE_CEILING_MULTIPLIER;
        return Math.min(Math.max(price, STOCK_PRICE_FLOOR), ceiling);
    }

    public static double applyDailySavingInterest(double currentAmount) {
        return currentAmount * DAILY_SAVING_INTEREST_RATE;
    }
}
