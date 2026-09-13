package com.stocktrading.service;

import com.stocktrading.exception.StockNotFoundException;
import com.stocktrading.model.Stock;
import com.stocktrading.repository.StockRepository;

import java.util.List;
import java.util.Random;

/**
 * Manages the simulated market: available stocks and price movements.
 */
public class MarketService {

    private final StockRepository stockRepository;
    private final Random random = new Random();

    public MarketService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        if (stockRepository.isEmpty()) {
            seedDefaultStocks();
        }
    }

    private void seedDefaultStocks() {
        stockRepository.addOrUpdate(new Stock("TCS", "Tata Consultancy Services", 3500.00, 3500.00));
        stockRepository.addOrUpdate(new Stock("INFY", "Infosys Ltd.", 1600.00, 1600.00));
        stockRepository.addOrUpdate(new Stock("RELIANCE", "Reliance Industries Ltd.", 2900.00, 2900.00));
        stockRepository.addOrUpdate(new Stock("HDFCBANK", "HDFC Bank Ltd.", 1700.00, 1700.00));
        stockRepository.addOrUpdate(new Stock("WIPRO", "Wipro Ltd.", 500.00, 500.00));
        stockRepository.addOrUpdate(new Stock("ITC", "ITC Ltd.", 450.00, 450.00));
        stockRepository.save();
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock getStock(String symbol) throws StockNotFoundException {
        return stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException("Stock '" + symbol + "' does not exist."));
    }

    /**
     * Simulates market movement by nudging every stock's price by a small
     * random percentage (between -3% and +3%). This lets the user demonstrate
     * changing market values without any external data feed.
     */
    public void simulatePriceMovement() {
        for (Stock stock : stockRepository.findAll()) {
            double percentChange = (random.nextDouble() * 6.0) - 3.0; // -3.0 .. +3.0
            double newPrice = stock.getCurrentPrice() * (1 + percentChange / 100.0);
            newPrice = Math.round(newPrice * 100.0) / 100.0;
            if (newPrice < 1.0) {
                newPrice = 1.0;
            }
            stock.updatePrice(newPrice);
        }
        stockRepository.save();
    }
}
