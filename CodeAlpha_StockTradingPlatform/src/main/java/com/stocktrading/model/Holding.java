package com.stocktrading.model;

/**
 * Represents a user's position (holding) in a particular stock:
 * how many shares they own and the average price they paid.
 */
public class Holding {

    private final String symbol;
    private int quantity;
    private double averageBuyPrice;

    public Holding(String symbol, int quantity, double averageBuyPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    /**
     * Adds shares to this holding, recalculating the weighted average buy price.
     */
    public void addShares(int additionalQuantity, double buyPrice) {
        double totalCostSoFar = this.averageBuyPrice * this.quantity;
        double newCost = buyPrice * additionalQuantity;
        this.quantity += additionalQuantity;
        this.averageBuyPrice = (totalCostSoFar + newCost) / this.quantity;
    }

    /**
     * Removes shares from this holding (on sale). Average buy price is unchanged.
     */
    public void removeShares(int quantityToRemove) {
        this.quantity -= quantityToRemove;
    }

    public double getInvestedAmount() {
        return quantity * averageBuyPrice;
    }

    public double getCurrentValue(double currentPrice) {
        return quantity * currentPrice;
    }

    public double getProfitLoss(double currentPrice) {
        return getCurrentValue(currentPrice) - getInvestedAmount();
    }

    public double getProfitLossPercent(double currentPrice) {
        if (getInvestedAmount() == 0) {
            return 0.0;
        }
        return (getProfitLoss(currentPrice) / getInvestedAmount()) * 100.0;
    }
}
