package com.stocktrading.ui;

import com.stocktrading.exception.InsufficientBalanceException;
import com.stocktrading.exception.InvalidQuantityException;
import com.stocktrading.exception.StockNotFoundException;
import com.stocktrading.model.Stock;
import com.stocktrading.model.User;
import com.stocktrading.util.CurrencyFormatter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog allowing the current user to buy shares of a selected stock.
 */
public class BuyStockDialog extends JDialog {

    private final AppContext context;
    private final Runnable onSuccess;

    private final JComboBox<String> stockCombo = new JComboBox<>();
    private final JTextField quantityField = new JTextField(8);
    private final JLabel priceLabel = new JLabel("-");
    private final JLabel totalLabel = new JLabel("-");

    public BuyStockDialog(Frame owner, AppContext context, Runnable onSuccess) {
        super(owner, "Buy Stock", true);
        this.context = context;
        this.onSuccess = onSuccess;
        buildUi();
    }

    private void buildUi() {
        setSize(380, 300);
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<Stock> stocks = context.marketService.getAllStocks();
        for (Stock s : stocks) {
            stockCombo.addItem(s.getSymbol());
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
        form.add(new JLabel("Current Price:"), gbc);
        gbc.gridx = 1;
        form.add(priceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        form.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Total Cost:"), gbc);
        gbc.gridx = 1;
        form.add(totalLabel, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton buyButton = new JButton("Confirm BUY");
        JButton cancelButton = new JButton("Cancel");
        buttons.add(buyButton);
        buttons.add(cancelButton);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);

        stockCombo.addActionListener(e -> updatePriceAndTotal());
        quantityField.addActionListener(e -> updatePriceAndTotal());
        quantityField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updatePriceAndTotal));

        buyButton.addActionListener(e -> attemptBuy());
        cancelButton.addActionListener(e -> dispose());

        updatePriceAndTotal();
    }

    private void updatePriceAndTotal() {
        String symbol = (String) stockCombo.getSelectedItem();
        if (symbol == null) {
            return;
        }
        try {
            Stock stock = context.marketService.getStock(symbol);
            priceLabel.setText(CurrencyFormatter.format(stock.getCurrentPrice()));
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

    private void attemptBuy() {
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
            context.tradingService.buy(user, symbol, quantity);
            JOptionPane.showMessageDialog(this, "Purchase successful!");
            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } catch (StockNotFoundException | InvalidQuantityException | InsufficientBalanceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Buy Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
