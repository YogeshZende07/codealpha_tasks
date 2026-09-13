package com.stocktrading.service;

import com.stocktrading.exception.InsufficientBalanceException;
import com.stocktrading.exception.InsufficientSharesException;
import com.stocktrading.exception.InvalidQuantityException;
import com.stocktrading.exception.StockNotFoundException;
import com.stocktrading.model.Holding;
import com.stocktrading.model.Stock;
import com.stocktrading.model.Transaction;
import com.stocktrading.model.User;

/**
 * Orchestrates BUY and SELL orders by coordinating the market, portfolio,
 * user (balance) and transaction-history services, applying all validation
 * rules in one place.
 */
public class TradingService {

    private final MarketService marketService;
    private final PortfolioService portfolioService;
    private final UserService userService;
    private final TransactionService transactionService;

    public TradingService(MarketService marketService, PortfolioService portfolioService,
                           UserService userService, TransactionService transactionService) {
        this.marketService = marketService;
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    public Transaction buy(User user, String symbol, int quantity)
            throws StockNotFoundException, InvalidQuantityException, InsufficientBalanceException {

        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be a positive whole number.");
        }

        Stock stock = marketService.getStock(symbol); // throws StockNotFoundException if missing

        double totalCost = quantity * stock.getCurrentPrice();
        if (totalCost > user.getBalance()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Required: " + String.format("%.2f", totalCost)
                            + ", Available: " + String.format("%.2f", user.getBalance()));
        }

        user.debit(totalCost);
        userService.persist(user);

        portfolioService.applyBuy(user.getId(), stock.getSymbol(), quantity, stock.getCurrentPrice());

        return transactionService.recordBuy(user.getId(), stock.getSymbol(), quantity, stock.getCurrentPrice());
    }

    public Transaction sell(User user, String symbol, int quantity)
            throws StockNotFoundException, InvalidQuantityException, InsufficientSharesException {

        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be a positive whole number.");
        }

        Stock stock = marketService.getStock(symbol); // throws StockNotFoundException if missing

        Holding holding = portfolioService.getHolding(user.getId(), symbol)
                .orElseThrow(() -> new InsufficientSharesException("You do not own any shares of " + symbol + "."));

        if (quantity > holding.getQuantity()) {
            throw new InsufficientSharesException(
                    "You only own " + holding.getQuantity() + " share(s) of " + symbol + ".");
        }

        double totalProceeds = quantity * stock.getCurrentPrice();

        portfolioService.applySell(user.getId(), stock.getSymbol(), quantity);

        user.credit(totalProceeds);
        userService.persist(user);

        return transactionService.recordSell(user.getId(), stock.getSymbol(), quantity, stock.getCurrentPrice());
    }
}
