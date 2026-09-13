package com.stocktrading.ui;

import com.stocktrading.model.Transaction;
import com.stocktrading.model.User;
import com.stocktrading.util.CurrencyFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Displays the full BUY/SELL transaction history for the current user.
 */
public class TransactionHistoryDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    public TransactionHistoryDialog(Frame owner, AppContext context) {
        super(owner, "Transaction History", true);
        buildUi(context);
    }

    private void buildUi(AppContext context) {
        setSize(750, 400);
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"Txn ID", "Date/Time", "Stock", "Type", "Quantity", "Price", "Total Amount"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        User user = context.userService.getCurrentUser();
        List<Transaction> history = context.transactionService.getHistory(user.getId());
        for (Transaction t : history) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getTimestamp().format(DATE_FORMAT),
                    t.getSymbol(),
                    t.getType(),
                    t.getQuantity(),
                    CurrencyFormatter.format(t.getPrice()),
                    CurrencyFormatter.format(t.getTotalAmount())
            });
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        if (history.isEmpty()) {
            root.add(new JLabel("No transactions yet.", SwingConstants.CENTER), BorderLayout.NORTH);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.add(closeButton);
        root.add(closePanel, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
