package com.stocktrading.model;

/**
 * Represents a single stock available on the simulated market.
 */
public class Stock {

    private final String symbol;
    private final String companyName;
    private double currentPrice;
    private double previousPrice;

    public Stock(String symbol, String companyName, double currentPrice, double previousPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.previousPrice = previousPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    /**
     * Updates the current price, shifting the old current price into previousPrice.
     */
    public void updatePrice(double newPrice) {
        this.previousPrice = this.currentPrice;
        this.currentPrice = newPrice;
    }

    /**
     * @return absolute change between current and previous price.
     */
    public double getChange() {
        return currentPrice - previousPrice;
    }

    /**
     * @return percentage change between current and previous price.
     */
    public double getPercentChange() {
        if (previousPrice == 0) {
            return 0.0;
        }
        return (getChange() / previousPrice) * 100.0;
    }

    @Override
    public String toString() {
        return symbol + " - " + companyName;
    }
}
