package com.stocktrading.ui;

import com.stocktrading.model.User;
import com.stocktrading.util.CurrencyFormatter;

import javax.swing.*;
import java.awt.*;

/**
 * The main hub screen shown after login. Provides an overview of the
 * account and quick navigation to every other screen in the application.
 */
public class DashboardFrame extends JFrame {

    private final AppContext context;

    private JLabel welcomeLabel;
    private JLabel balanceLabel;
    private JLabel portfolioValueLabel;
    private JLabel profitLossLabel;
    private JLabel totalAccountValueLabel;

    public DashboardFrame(AppContext context) {
        super("Stock Trading Platform - Dashboard");
        this.context = context;
        buildUi();
        refreshSummary();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(welcomeLabel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Account Summary"));

        balanceLabel = new JLabel();
        portfolioValueLabel = new JLabel();
        profitLossLabel = new JLabel();
        totalAccountValueLabel = new JLabel();

        summaryPanel.add(new JLabel("Available Cash Balance:"));
        summaryPanel.add(balanceLabel);
        summaryPanel.add(new JLabel("Current Portfolio Value:"));
        summaryPanel.add(portfolioValueLabel);
        summaryPanel.add(new JLabel("Total Profit / Loss:"));
        summaryPanel.add(profitLossLabel);
        summaryPanel.add(new JLabel("Total Account Value:"));
        summaryPanel.add(totalAccountValueLabel);

        root.add(summaryPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton marketDataButton = new JButton("Market Data");
        JButton buyButton = new JButton("Buy Stock");
        JButton sellButton = new JButton("Sell Stock");
        JButton portfolioButton = new JButton("View Portfolio");
        JButton historyButton = new JButton("Transaction History");
        JButton performanceButton = new JButton("Portfolio Performance");
        JButton refreshMarketButton = new JButton("Simulate Market Update");
        JButton refreshButton = new JButton("Refresh Summary");
        JButton logoutButton = new JButton("Logout");

        actionsPanel.add(marketDataButton);
        actionsPanel.add(buyButton);
        actionsPanel.add(sellButton);
        actionsPanel.add(portfolioButton);
        actionsPanel.add(historyButton);
        actionsPanel.add(performanceButton);
        actionsPanel.add(refreshMarketButton);
        actionsPanel.add(refreshButton);
        actionsPanel.add(logoutButton);

        root.add(actionsPanel, BorderLayout.SOUTH);

        setContentPane(root);

        marketDataButton.addActionListener(e -> new MarketDataDialog(this, context).setVisible(true));
        buyButton.addActionListener(e -> {
            new BuyStockDialog(this, context, this::refreshSummary).setVisible(true);
        });
        sellButton.addActionListener(e -> {
            new SellStockDialog(this, context, this::refreshSummary).setVisible(true);
        });
        portfolioButton.addActionListener(e -> new PortfolioDialog(this, context).setVisible(true));
        historyButton.addActionListener(e -> new TransactionHistoryDialog(this, context).setVisible(true));
        performanceButton.addActionListener(e -> new PerformanceDialog(this, context).setVisible(true));
        refreshMarketButton.addActionListener(e -> {
            context.marketService.simulatePriceMovement();
            JOptionPane.showMessageDialog(this, "Market prices updated.");
            refreshSummary();
        });
        refreshButton.addActionListener(e -> refreshSummary());
        logoutButton.addActionListener(e -> {
            User user = context.userService.getCurrentUser();
            if (user != null) {
                context.portfolioService.recordSnapshot(user.getId(), user.getBalance());
            }
            context.userService.logout();
            new LoginFrame(context).setVisible(true);
            this.dispose();
        });
    }

    /**
     * Refreshes all summary labels from the latest user/portfolio state.
     */
    public void refreshSummary() {
        User user = context.userService.getCurrentUser();
        if (user == null) {
            return;
        }
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");

        double portfolioValue = context.portfolioService.getTotalPortfolioValue(user.getId());
        double profitLoss = context.portfolioService.getTotalProfitLoss(user.getId());
        double totalAccountValue = user.getBalance() + portfolioValue;

        balanceLabel.setText(CurrencyFormatter.format(user.getBalance()));
        portfolioValueLabel.setText(CurrencyFormatter.format(portfolioValue));
        profitLossLabel.setText(CurrencyFormatter.format(profitLoss));
        profitLossLabel.setForeground(profitLoss >= 0 ? new Color(0, 128, 0) : Color.RED);
        totalAccountValueLabel.setText(CurrencyFormatter.format(totalAccountValue));
    }
}
