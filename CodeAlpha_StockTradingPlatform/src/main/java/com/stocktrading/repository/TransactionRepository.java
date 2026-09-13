package com.stocktrading.repository;

import com.stocktrading.model.BuyTransaction;
import com.stocktrading.model.SellTransaction;
import com.stocktrading.model.Transaction;
import com.stocktrading.model.TransactionType;
import com.stocktrading.util.CsvUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based persistence for the transaction (trade) history.
 */
public class TransactionRepository {

    private static final Path FILE_PATH = Path.of("data", "transactions.csv");
    private static final String HEADER = "id,userId,type,symbol,quantity,price,timestamp";

    private final List<Transaction> transactions = new ArrayList<>();
    private int nextId = 1;

    public TransactionRepository() {
        load();
    }

    private void load() {
        transactions.clear();
        for (String[] cols : CsvUtil.readRows(FILE_PATH)) {
            int id = Integer.parseInt(cols[0]);
            int userId = Integer.parseInt(cols[1]);
            TransactionType type = TransactionType.valueOf(cols[2]);
            String symbol = cols[3];
            int quantity = Integer.parseInt(cols[4]);
            double price = Double.parseDouble(cols[5]);
            LocalDateTime timestamp = LocalDateTime.parse(cols[6]);

            Transaction t = (type == TransactionType.BUY)
                    ? new BuyTransaction(id, userId, symbol, quantity, price, timestamp)
                    : new SellTransaction(id, userId, symbol, quantity, price, timestamp);
            transactions.add(t);
            if (id >= nextId) {
                nextId = id + 1;
            }
        }
    }

    public void save() {
        List<String> rows = new ArrayList<>();
        for (Transaction t : transactions) {
            rows.add(String.join(",",
                    String.valueOf(t.getId()),
                    String.valueOf(t.getUserId()),
                    t.getType().name(),
                    t.getSymbol(),
                    String.valueOf(t.getQuantity()),
                    String.valueOf(t.getPrice()),
                    t.getTimestamp().toString()));
        }
        CsvUtil.writeRows(FILE_PATH, HEADER, rows);
    }

    public Transaction recordBuy(int userId, String symbol, int quantity, double price) {
        Transaction t = new BuyTransaction(nextId++, userId, symbol, quantity, price, LocalDateTime.now());
        transactions.add(t);
        save();
        return t;
    }

    public Transaction recordSell(int userId, String symbol, int quantity, double price) {
        Transaction t = new SellTransaction(nextId++, userId, symbol, quantity, price, LocalDateTime.now());
        transactions.add(t);
        save();
        return t;
    }

    public List<Transaction> findByUser(int userId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getUserId() == userId) {
                result.add(t);
            }
        }
        return result;
    }
}
