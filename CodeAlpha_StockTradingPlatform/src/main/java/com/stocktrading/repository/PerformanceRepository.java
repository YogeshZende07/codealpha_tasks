package com.stocktrading.repository;

import com.stocktrading.model.PortfolioSnapshot;
import com.stocktrading.util.CsvUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based persistence for historical portfolio value snapshots, used to
 * show performance/return over time.
 */
public class PerformanceRepository {

    private static final Path FILE_PATH = Path.of("data", "portfolio_performance.csv");
    private static final String HEADER = "userId,timestamp,portfolioValue,cashBalance,totalValue";

    private final List<PortfolioSnapshot> snapshots = new ArrayList<>();

    public PerformanceRepository() {
        load();
    }

    private void load() {
        snapshots.clear();
        for (String[] cols : CsvUtil.readRows(FILE_PATH)) {
            int userId = Integer.parseInt(cols[0]);
            LocalDateTime timestamp = LocalDateTime.parse(cols[1]);
            double portfolioValue = Double.parseDouble(cols[2]);
            double cashBalance = Double.parseDouble(cols[3]);
            double totalValue = Double.parseDouble(cols[4]);
            snapshots.add(new PortfolioSnapshot(userId, timestamp, portfolioValue, cashBalance, totalValue));
        }
    }

    public void save() {
        List<String> rows = new ArrayList<>();
        for (PortfolioSnapshot s : snapshots) {
            rows.add(String.join(",",
                    String.valueOf(s.getUserId()),
                    s.getTimestamp().toString(),
                    String.valueOf(s.getPortfolioValue()),
                    String.valueOf(s.getCashBalance()),
                    String.valueOf(s.getTotalValue())));
        }
        CsvUtil.writeRows(FILE_PATH, HEADER, rows);
    }

    public void addSnapshot(PortfolioSnapshot snapshot) {
        snapshots.add(snapshot);
        save();
    }

    public List<PortfolioSnapshot> findByUser(int userId) {
        List<PortfolioSnapshot> result = new ArrayList<>();
        for (PortfolioSnapshot s : snapshots) {
            if (s.getUserId() == userId) {
                result.add(s);
            }
        }
        return result;
    }
}
