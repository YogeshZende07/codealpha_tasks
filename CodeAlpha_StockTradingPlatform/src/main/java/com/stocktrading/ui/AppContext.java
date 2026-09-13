package com.stocktrading.ui;

import com.stocktrading.service.MarketService;
import com.stocktrading.service.PortfolioService;
import com.stocktrading.service.TradingService;
import com.stocktrading.service.TransactionService;
import com.stocktrading.service.UserService;

/**
 * Simple dependency container bundling all services together so Swing
 * screens don't need long constructor parameter lists.
 */
public class AppContext {

    public final UserService userService;
    public final MarketService marketService;
    public final PortfolioService portfolioService;
    public final TransactionService transactionService;
    public final TradingService tradingService;

    public AppContext(UserService userService, MarketService marketService, PortfolioService portfolioService,
                       TransactionService transactionService, TradingService tradingService) {
        this.userService = userService;
        this.marketService = marketService;
        this.portfolioService = portfolioService;
        this.transactionService = transactionService;
        this.tradingService = tradingService;
    }
}
