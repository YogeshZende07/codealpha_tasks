package com.stocktrading.ui;

import com.stocktrading.model.Stock;
import com.stocktrading.util.CurrencyFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Displays live (simulated) market data for every available stock.
 */
public class MarketDataDialog extends JDialog {

    private final AppContext context;
    private DefaultTableModel tableModel;

    public MarketDataDialog(Frame owner, AppContext context) {
        super(owner, "Market Data", true);
        this.context = context;
        buildUi();
        loadData();
    }

    private void buildUi() {
        setSize(650, 350);
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"Symbol", "Company Name", "Current Price", "Previous Price", "Change", "% Change"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton simulateButton = new JButton("Simulate Market Update");
        JButton closeButton = new JButton("Close");
        buttons.add(simulateButton);
        buttons.add(closeButton);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);

        simulateButton.addActionListener(e -> {
            context.marketService.simulatePriceMovement();
            loadData();
        });
        closeButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Stock> stocks = context.marketService.getAllStocks();
        for (Stock s : stocks) {
            tableModel.addRow(new Object[]{
                    s.getSymbol(),
                    s.getCompanyName(),
                    CurrencyFormatter.format(s.getCurrentPrice()),
                    CurrencyFormatter.format(s.getPreviousPrice()),
                    CurrencyFormatter.format(s.getChange()),
                    CurrencyFormatter.formatPercent(s.getPercentChange())
            });
        }
    }
}
