package com.stocktrading.service;

import com.stocktrading.model.Transaction;
import com.stocktrading.repository.TransactionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Records and retrieves the user's transaction (trade) history.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction recordBuy(int userId, String symbol, int quantity, double price) {
        return transactionRepository.recordBuy(userId, symbol, quantity, price);
    }

    public Transaction recordSell(int userId, String symbol, int quantity, double price) {
        return transactionRepository.recordSell(userId, symbol, quantity, price);
    }

    /**
     * @return the user's transactions, most recent first.
     */
    public List<Transaction> getHistory(int userId) {
        return transactionRepository.findByUser(userId).stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
