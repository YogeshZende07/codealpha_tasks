package com.stocktrading.model;

import java.time.LocalDateTime;

/**
 * A point-in-time snapshot of a user's total portfolio value, used to track
 * performance over time.
 */
public class PortfolioSnapshot {

    private final int userId;
    private final LocalDateTime timestamp;
    private final double portfolioValue;
    private final double cashBalance;
    private final double totalValue;

    public PortfolioSnapshot(int userId, LocalDateTime timestamp, double portfolioValue, double cashBalance, double totalValue) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.portfolioValue = portfolioValue;
        this.cashBalance = cashBalance;
        this.totalValue = totalValue;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
