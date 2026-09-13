package com.stocktrading.service;

import com.stocktrading.exception.StockNotFoundException;
import com.stocktrading.model.Holding;
import com.stocktrading.model.PortfolioSnapshot;
import com.stocktrading.model.Stock;
import com.stocktrading.repository.HoldingRepository;
import com.stocktrading.repository.PerformanceRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Manages a user's portfolio: their holdings and aggregate performance figures.
 */
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final PerformanceRepository performanceRepository;
    private final MarketService marketService;

    public PortfolioService(HoldingRepository holdingRepository, PerformanceRepository performanceRepository, MarketService marketService) {
        this.holdingRepository = holdingRepository;
        this.performanceRepository = performanceRepository;
        this.marketService = marketService;
    }

    public Collection<Holding> getHoldings(int userId) {
        return holdingRepository.getHoldingsForUser(userId).values();
    }

    public Optional<Holding> getHolding(int userId, String symbol) {
        return holdingRepository.findHolding(userId, symbol);
    }

    /**
     * Adds shares to a user's holding after a BUY, creating the holding if needed.
     */
    public void applyBuy(int userId, String symbol, int quantity, double price) {
        Holding holding = holdingRepository.findHolding(userId, symbol).orElse(null);
        if (holding == null) {
            holding = new Holding(symbol, quantity, price);
        } else {
            holding.addShares(quantity, price);
        }
        holdingRepository.upsertHolding(userId, holding);
        holdingRepository.save();
    }

    /**
     * Removes shares from a user's holding after a SELL.
     */
    public void applySell(int userId, String symbol, int quantity) {
        Holding holding = holdingRepository.findHolding(userId, symbol)
                .orElseThrow(() -> new IllegalStateException("No holding found for " + symbol));
        holding.removeShares(quantity);
        holdingRepository.upsertHolding(userId, holding);
        holdingRepository.removeIfEmpty(userId, symbol);
        holdingRepository.save();
    }

    /**
     * @return the current total market value of all of a user's holdings.
     */
    public double getTotalPortfolioValue(int userId) {
        double total = 0.0;
        for (Holding h : getHoldings(userId)) {
            try {
                Stock stock = marketService.getStock(h.getSymbol());
                total += h.getCurrentValue(stock.getCurrentPrice());
            } catch (StockNotFoundException e) {
                // stock no longer exists on the market; skip it
            }
        }
        return total;
    }

    public double getTotalInvestedAmount(int userId) {
        double total = 0.0;
        for (Holding h : getHoldings(userId)) {
            total += h.getInvestedAmount();
        }
        return total;
    }

    public double getTotalProfitLoss(int userId) {
        return getTotalPortfolioValue(userId) - getTotalInvestedAmount(userId);
    }

    /**
     * Records a point-in-time snapshot of the user's portfolio value so that
     * performance over time can be tracked.
     */
    public PortfolioSnapshot recordSnapshot(int userId, double cashBalance) {
        double portfolioValue = getTotalPortfolioValue(userId);
        double totalValue = portfolioValue + cashBalance;
        PortfolioSnapshot snapshot = new PortfolioSnapshot(userId, LocalDateTime.now(), portfolioValue, cashBalance, totalValue);
        performanceRepository.addSnapshot(snapshot);
        return snapshot;
    }

    public List<PortfolioSnapshot> getPerformanceHistory(int userId) {
        return new ArrayList<>(performanceRepository.findByUser(userId));
    }
}
