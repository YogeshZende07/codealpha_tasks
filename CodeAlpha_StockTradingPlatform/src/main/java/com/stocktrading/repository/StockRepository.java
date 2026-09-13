package com.stocktrading.repository;

import com.stocktrading.model.Stock;
import com.stocktrading.util.CsvUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * File-based persistence for Stock (market data) records.
 */
public class StockRepository {

    private static final Path FILE_PATH = Path.of("data", "stocks.csv");
    private static final String HEADER = "symbol,companyName,currentPrice,previousPrice";

    private final Map<String, Stock> stocks = new LinkedHashMap<>();

    public StockRepository() {
        load();
    }

    private void load() {
        stocks.clear();
        for (String[] cols : CsvUtil.readRows(FILE_PATH)) {
            String symbol = cols[0];
            String companyName = cols[1];
            double currentPrice = Double.parseDouble(cols[2]);
            double previousPrice = Double.parseDouble(cols[3]);
            stocks.put(symbol, new Stock(symbol, companyName, currentPrice, previousPrice));
        }
    }

    public void save() {
        List<String> rows = new ArrayList<>();
        for (Stock s : stocks.values()) {
            rows.add(String.join(",",
                    s.getSymbol(),
                    s.getCompanyName(),
                    String.valueOf(s.getCurrentPrice()),
                    String.valueOf(s.getPreviousPrice())));
        }
        CsvUtil.writeRows(FILE_PATH, HEADER, rows);
    }

    public List<Stock> findAll() {
        return new ArrayList<>(stocks.values());
    }

    public Optional<Stock> findBySymbol(String symbol) {
        return Optional.ofNullable(stocks.get(symbol));
    }

    public boolean isEmpty() {
        return stocks.isEmpty();
    }

    public void addOrUpdate(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }
}
