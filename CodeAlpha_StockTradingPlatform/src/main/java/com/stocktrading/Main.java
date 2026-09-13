package com.stocktrading;

import com.stocktrading.repository.HoldingRepository;
import com.stocktrading.repository.PerformanceRepository;
import com.stocktrading.repository.StockRepository;
import com.stocktrading.repository.TransactionRepository;
import com.stocktrading.repository.UserRepository;
import com.stocktrading.service.MarketService;
import com.stocktrading.service.PortfolioService;
import com.stocktrading.service.TradingService;
import com.stocktrading.service.TransactionService;
import com.stocktrading.service.UserService;
import com.stocktrading.ui.AppContext;
import com.stocktrading.ui.LoginFrame;

import javax.swing.*;

/**
 * Application entry point. Wires up repositories and services, seeds demo
 * data on first run, and launches the login screen.
 */
public class Main {

    public static void main(String[] args) {
        // Repositories (file-based persistence under the ./data directory)
        UserRepository userRepository = new UserRepository();
        StockRepository stockRepository = new StockRepository();
        HoldingRepository holdingRepository = new HoldingRepository();
        TransactionRepository transactionRepository = new TransactionRepository();
        PerformanceRepository performanceRepository = new PerformanceRepository();

        // Services
        UserService userService = new UserService(userRepository);
        MarketService marketService = new MarketService(stockRepository); // seeds default stocks if needed
        PortfolioService portfolioService = new PortfolioService(holdingRepository, performanceRepository, marketService);
        TransactionService transactionService = new TransactionService(transactionRepository);
        TradingService tradingService = new TradingService(marketService, portfolioService, userService, transactionService);

        // Seed a demo user on first run so the app can be demonstrated immediately
        if (!userRepository.exists("demo")) {
            userRepository.create("demo", "demo123", 100000.0);
        }

        AppContext context = new AppContext(userService, marketService, portfolioService, transactionService, tradingService);

        SwingUtilities.invokeLater(() -> new LoginFrame(context).setVisible(true));
    }
}
