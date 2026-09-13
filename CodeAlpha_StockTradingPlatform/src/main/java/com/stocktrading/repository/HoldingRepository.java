package com.stocktrading.repository;

import com.stocktrading.model.Holding;
import com.stocktrading.util.CsvUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * File-based persistence for each user's stock holdings (their portfolio positions).
 */
public class HoldingRepository {

    private static final Path FILE_PATH = Path.of("data", "holdings.csv");
    private static final String HEADER = "userId,symbol,quantity,averageBuyPrice";

    // userId -> (symbol -> Holding)
    private final Map<Integer, Map<String, Holding>> holdingsByUser = new LinkedHashMap<>();

    public HoldingRepository() {
        load();
    }

    private void load() {
        holdingsByUser.clear();
        for (String[] cols : CsvUtil.readRows(FILE_PATH)) {
            int userId = Integer.parseInt(cols[0]);
            String symbol = cols[1];
            int quantity = Integer.parseInt(cols[2]);
            double avgPrice = Double.parseDouble(cols[3]);
            holdingsByUser
                    .computeIfAbsent(userId, k -> new LinkedHashMap<>())
                    .put(symbol, new Holding(symbol, quantity, avgPrice));
        }
    }

    public void save() {
        List<String> rows = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Holding>> entry : holdingsByUser.entrySet()) {
            int userId = entry.getKey();
            for (Holding h : entry.getValue().values()) {
                if (h.getQuantity() <= 0) {
                    continue; // don't persist fully-sold-out positions
                }
                rows.add(String.join(",",
                        String.valueOf(userId),
                        h.getSymbol(),
                        String.valueOf(h.getQuantity()),
                        String.valueOf(h.getAverageBuyPrice())));
            }
        }
        CsvUtil.writeRows(FILE_PATH, HEADER, rows);
    }

    public Map<String, Holding> getHoldingsForUser(int userId) {
        return holdingsByUser.computeIfAbsent(userId, k -> new LinkedHashMap<>());
    }

    public Optional<Holding> findHolding(int userId, String symbol) {
        return Optional.ofNullable(getHoldingsForUser(userId).get(symbol));
    }

    public void upsertHolding(int userId, Holding holding) {
        getHoldingsForUser(userId).put(holding.getSymbol(), holding);
    }

    public void removeIfEmpty(int userId, String symbol) {
        Holding h = getHoldingsForUser(userId).get(symbol);
        if (h != null && h.getQuantity() <= 0) {
            getHoldingsForUser(userId).remove(symbol);
        }
    }
}
