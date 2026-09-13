package com.stocktrading.ui;

import com.stocktrading.model.PortfolioSnapshot;
import com.stocktrading.model.User;
import com.stocktrading.util.CurrencyFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Displays portfolio performance over time as a table of historical
 * snapshots (previous value vs current value, overall return), plus a
 * button to record a new snapshot on demand. A table-based history is used
 * instead of a charting library to keep the project dependency-free and
 * simple to run, per the fallback approach for performance tracking.
 */
public class PerformanceDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    private final AppContext context;
    private DefaultTableModel tableModel;
    private JLabel previousValueLabel;
    private JLabel currentValueLabel;
    private JLabel overallPlLabel;
    private JLabel returnPercentLabel;

    public PerformanceDialog(Frame owner, AppContext context) {
        super(owner, "Portfolio Performance", true);
        this.context = context;
        buildUi();
        refreshData();
    }

    private void buildUi() {
        setSize(700, 450);
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 6, 4));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Performance Summary"));
        previousValueLabel = new JLabel("-");
        currentValueLabel = new JLabel("-");
        overallPlLabel = new JLabel("-");
        returnPercentLabel = new JLabel("-");
        summaryPanel.add(new JLabel("Previous Portfolio Value (Total Account Value):"));
        summaryPanel.add(previousValueLabel);
        summaryPanel.add(new JLabel("Current Portfolio Value (Total Account Value):"));
        summaryPanel.add(currentValueLabel);
        summaryPanel.add(new JLabel("Overall Profit / Loss:"));
        summaryPanel.add(overallPlLabel);
        summaryPanel.add(new JLabel("Percentage Return:"));
        summaryPanel.add(returnPercentLabel);
        root.add(summaryPanel, BorderLayout.NORTH);

        String[] columns = {"Timestamp", "Portfolio Value", "Cash Balance", "Total Account Value"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton recordButton = new JButton("Record Snapshot Now");
        JButton closeButton = new JButton("Close");
        buttons.add(recordButton);
        buttons.add(closeButton);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);

        recordButton.addActionListener(e -> {
            User user = context.userService.getCurrentUser();
            context.portfolioService.recordSnapshot(user.getId(), user.getBalance());
            refreshData();
        });
        closeButton.addActionListener(e -> dispose());
    }

    private void refreshData() {
        User user = context.userService.getCurrentUser();

        double currentPortfolioValue = context.portfolioService.getTotalPortfolioValue(user.getId());
        double currentTotalValue = currentPortfolioValue + user.getBalance();

        List<PortfolioSnapshot> history = context.portfolioService.getPerformanceHistory(user.getId());

        tableModel.setRowCount(0);
        for (PortfolioSnapshot s : history) {
            tableModel.addRow(new Object[]{
                    s.getTimestamp().format(DATE_FORMAT),
                    CurrencyFormatter.format(s.getPortfolioValue()),
                    CurrencyFormatter.format(s.getCashBalance()),
                    CurrencyFormatter.format(s.getTotalValue())
            });
        }

        double previousTotalValue = history.isEmpty()
                ? user.getStartingBalance()
                : history.get(history.size() - 1).getTotalValue();

        double overallPl = currentTotalValue - user.getStartingBalance();
        double returnPercent = user.getStartingBalance() == 0
                ? 0.0
                : (overallPl / user.getStartingBalance()) * 100.0;

        previousValueLabel.setText(CurrencyFormatter.format(previousTotalValue));
        currentValueLabel.setText(CurrencyFormatter.format(currentTotalValue));
        overallPlLabel.setText(CurrencyFormatter.format(overallPl));
        overallPlLabel.setForeground(overallPl >= 0 ? new Color(0, 128, 0) : Color.RED);
        returnPercentLabel.setText(CurrencyFormatter.formatPercent(returnPercent));
    }
}
