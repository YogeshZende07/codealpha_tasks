package com.stocktrading.ui;

import com.stocktrading.exception.InsufficientSharesException;
import com.stocktrading.exception.InvalidQuantityException;
import com.stocktrading.exception.StockNotFoundException;
import com.stocktrading.model.Holding;
import com.stocktrading.model.Stock;
import com.stocktrading.model.User;
import com.stocktrading.util.CurrencyFormatter;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Dialog allowing the current user to sell shares of a stock they own.
 */
public class SellStockDialog extends JDialog {

    private final AppContext context;
    private final Runnable onSuccess;

    private final JComboBox<String> stockCombo = new JComboBox<>();
    private final JTextField quantityField = new JTextField(8);
    private final JLabel priceLabel = new JLabel("-");
    private final JLabel ownedLabel = new JLabel("-");
    private final JLabel totalLabel = new JLabel("-");

    public SellStockDialog(Frame owner, AppContext context, Runnable onSuccess) {
        super(owner, "Sell Stock", true);
        this.context = context;
        this.onSuccess = onSuccess;
        buildUi();
    }

    private void buildUi() {
        setSize(380, 330);
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        User user = context.userService.getCurrentUser();
        Collection<Holding> holdings = context.portfolioService.getHoldings(user.getId());
        for (Holding h : holdings) {
            if (h.getQuantity() > 0) {
                stockCombo.addItem(h.getSymbol());
            }
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        form.add(stockCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Shares Owned:"), gbc);
        gbc.gridx = 1;
        form.add(ownedLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Current Price:"), gbc);
        gbc.gridx = 1;
        form.add(priceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Quantity to Sell:"), gbc);
        gbc.gridx = 1;
        form.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Total Proceeds:"), gbc);
        gbc.gridx = 1;
        form.add(totalLabel, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton sellButton = new JButton("Confirm SELL");
        JButton cancelButton = new JButton("Cancel");
        buttons.add(sellButton);
        buttons.add(cancelButton);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);

        if (stockCombo.getItemCount() == 0) {
            sellButton.setEnabled(false);
            ownedLabel.setText("You have no holdings to sell.");
        }

        stockCombo.addActionListener(e -> updateDetails());
        quantityField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateDetails));

        sellButton.addActionListener(e -> attemptSell());
        cancelButton.addActionListener(e -> dispose());

        updateDetails();
    }

    private void updateDetails() {
        String symbol = (String) stockCombo.getSelectedItem();
        if (symbol == null) {
            return;
        }
        User user = context.userService.getCurrentUser();
        try {
            Stock stock = context.marketService.getStock(symbol);
            priceLabel.setText(CurrencyFormatter.format(stock.getCurrentPrice()));

            int owned = context.portfolioService.getHolding(user.getId(), symbol)
                    .map(Holding::getQuantity).orElse(0);
            ownedLabel.setText(String.valueOf(owned));

            int quantity = parseQuantityOrZero();
            totalLabel.setText(CurrencyFormatter.format(quantity * stock.getCurrentPrice()));
        } catch (StockNotFoundException e) {
            priceLabel.setText("-");
            totalLabel.setText("-");
        }
    }

    private int parseQuantityOrZero() {
        try {
            return Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void attemptSell() {
        String symbol = (String) stockCombo.getSelectedItem();
        if (symbol == null) {
            JOptionPane.showMessageDialog(this, "Please select a stock.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = context.userService.getCurrentUser();

        try {
            context.tradingService.sell(user, symbol, quantity);
            JOptionPane.showMessageDialog(this, "Sale successful!");
            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } catch (StockNotFoundException | InvalidQuantityException | InsufficientSharesException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Sell Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
