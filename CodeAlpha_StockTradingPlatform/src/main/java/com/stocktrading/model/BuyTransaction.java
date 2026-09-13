package com.stocktrading.model;

import java.time.LocalDateTime;

/**
 * A BUY transaction: shares purchased by a user.
 */
public class BuyTransaction extends Transaction {

    public BuyTransaction(int id, int userId, String symbol, int quantity, double price, LocalDateTime timestamp) {
        super(id, userId, symbol, quantity, price, timestamp);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.BUY;
    }
}
