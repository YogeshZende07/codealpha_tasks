package com.stocktrading.model;

import java.time.LocalDateTime;

/**
 * A SELL transaction: shares sold by a user.
 */
public class SellTransaction extends Transaction {

    public SellTransaction(int id, int userId, String symbol, int quantity, double price, LocalDateTime timestamp) {
        super(id, userId, symbol, quantity, price, timestamp);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.SELL;
    }
}
