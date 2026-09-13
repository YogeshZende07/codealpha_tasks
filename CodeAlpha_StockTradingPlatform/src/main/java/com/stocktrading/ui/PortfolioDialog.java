package com.stocktrading.ui;

import com.stocktrading.exception.StockNotFoundException;
import com.stocktrading.model.Holding;
import com.stocktrading.model.Stock;
import com.stocktrading.model.User;
import com.stocktrading.util.CurrencyFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collection;

/**
 * Displays the current user's portfolio: individual holdings plus aggregate totals.
 */
public class PortfolioDialog extends JDialog {

    private final AppContext context;

    public PortfolioDialog(Frame owner, AppContext context) {
        super(owner, "My Portfolio", true);
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setSize(800, 420);
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"Symbol", "Company", "Qty", "Avg Buy Price", "Current Price",
                "Invested Amount", "Current Value", "P/L", "P/L %"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        User user = context.userService.getCurrentUser();
        Collection<Holding> holdings = context.portfolioService.getHoldings(user.getId());

        double totalInvested = 0;
        double totalCurrentValue = 0;

        for (Holding h : holdings) {
            if (h.getQuantity() <= 0) {
                continue;
            }
            try {
                Stock stock = context.marketService.getStock(h.getSymbol());
                double currentValue = h.getCurrentValue(stock.getCurrentPrice());
                double invested = h.getInvestedAmount();
                double pl = h.getProfitLoss(stock.getCurrentPrice());
                double plPercent = h.getProfitLossPercent(stock.getCurrentPrice());

                totalInvested += invested;
                totalCurrentValue += currentValue;

                tableModel.addRow(new Object[]{
                        h.getSymbol(),
                        stock.getCompanyName(),
                        h.getQuantity(),
                        CurrencyFormatter.format(h.getAverageBuyPrice()),
                        CurrencyFormatter.format(stock.getCurrentPrice()),
                        CurrencyFormatter.format(invested),
                        CurrencyFormatter.format(currentValue),
                        CurrencyFormatter.format(pl),
                        CurrencyFormatter.formatPercent(plPercent)
                });
            } catch (StockNotFoundException e) {
                // stock no longer listed; skip
            }
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        double totalProfitLoss = totalCurrentValue - totalInvested;
        double totalAccountValue = user.getBalance() + totalCurrentValue;

        JPanel summaryPanel = new JPanel(new GridLayout(5, 2, 6, 4));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.add(new JLabel("Total Invested Amount:"));
        summaryPanel.add(new JLabel(CurrencyFormatter.format(totalInvested)));
        summaryPanel.add(new JLabel("Current Portfolio Value:"));
        summaryPanel.add(new JLabel(CurrencyFormatter.format(totalCurrentValue)));
        summaryPanel.add(new JLabel("Total Profit / Loss:"));
        summaryPanel.add(new JLabel(CurrencyFormatter.format(totalProfitLoss)));
        summaryPanel.add(new JLabel("Available Cash Balance:"));
        summaryPanel.add(new JLabel(CurrencyFormatter.format(user.getBalance())));
        summaryPanel.add(new JLabel("Total Account Value:"));
        summaryPanel.add(new JLabel(CurrencyFormatter.format(totalAccountValue)));

        root.add(summaryPanel, BorderLayout.SOUTH);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.add(closeButton);
        root.add(closePanel, BorderLayout.NORTH);

        setContentPane(root);
    }
}
