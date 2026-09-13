package com.stocktrading.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic unit tests for the Holding model's average-price and P/L calculations.
 */
class HoldingTest {

    @Test
    void addSharesRecalculatesWeightedAveragePrice() {
        Holding holding = new Holding("TCS", 10, 3000.0);
        holding.addShares(10, 3200.0);

        assertEquals(20, holding.getQuantity());
        assertEquals(3100.0, holding.getAverageBuyPrice(), 0.001);
    }

    @Test
    void removeSharesReducesQuantityOnly() {
        Holding holding = new Holding("INFY", 10, 1500.0);
        holding.removeShares(4);

        assertEquals(6, holding.getQuantity());
        assertEquals(1500.0, holding.getAverageBuyPrice(), 0.001);
    }

    @Test
    void profitLossIsCalculatedCorrectly() {
        Holding holding = new Holding("WIPRO", 100, 500.0);

        assertEquals(50000.0, holding.getInvestedAmount(), 0.001);
        assertEquals(2000.0, holding.getProfitLoss(520.0), 0.001);
        assertEquals(4.0, holding.getProfitLossPercent(520.0), 0.001);
    }
}
