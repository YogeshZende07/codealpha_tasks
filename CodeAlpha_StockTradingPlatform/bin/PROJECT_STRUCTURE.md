# Project Structure

```
Stock-Trading-Platform/
│
├── pom.xml                     Maven build file (Java 21, JUnit 5)
├── README.md                   Full project documentation
├── PROJECT_STRUCTURE.md        This file
├── database.sql                Optional reference PostgreSQL schema (not used by default)
├── .gitignore
│
├── data/                       Generated at runtime - CSV persistence files
│   ├── users.csv
│   ├── stocks.csv
│   ├── holdings.csv
│   ├── transactions.csv
│   └── portfolio_performance.csv
│
└── src/
    ├── main/java/com/stocktrading/
    │   ├── Main.java                    Application entry point - wires everything together
    │   │
    │   ├── model/                       Plain data classes (OOP core)
    │   │   ├── User.java
    │   │   ├── Stock.java
    │   │   ├── Holding.java
    │   │   ├── Transaction.java         abstract base class
    │   │   ├── BuyTransaction.java      extends Transaction
    │   │   ├── SellTransaction.java     extends Transaction
    │   │   ├── TransactionType.java     enum: BUY, SELL
    │   │   ├── Order.java
    │   │   └── PortfolioSnapshot.java
    │   │
    │   ├── service/                     Business logic
    │   │   ├── UserService.java         register / login / logout
    │   │   ├── MarketService.java       market data + simulated price movement
    │   │   ├── PortfolioService.java    holdings, valuations, performance snapshots
    │   │   ├── TransactionService.java  transaction history
    │   │   └── TradingService.java      orchestrates BUY/SELL with validation
    │   │
    │   ├── repository/                  File-based persistence (CSV under ./data)
    │   │   ├── UserRepository.java
    │   │   ├── StockRepository.java
    │   │   ├── HoldingRepository.java
    │   │   ├── TransactionRepository.java
    │   │   └── PerformanceRepository.java
    │   │
    │   ├── exception/                   Custom checked exceptions
    │   │   ├── AuthenticationException.java
    │   │   ├── UserAlreadyExistsException.java
    │   │   ├── StockNotFoundException.java
    │   │   ├── InvalidQuantityException.java
    │   │   ├── InsufficientBalanceException.java
    │   │   └── InsufficientSharesException.java
    │   │
    │   ├── ui/                          Java Swing screens
    │   │   ├── AppContext.java          bundles all services for the UI layer
    │   │   ├── LoginFrame.java          Screen 1: Login
    │   │   ├── RegisterDialog.java      Screen 2: Register
    │   │   ├── DashboardFrame.java      Screen 3: Dashboard
    │   │   ├── MarketDataDialog.java    Screen 4: Market Data
    │   │   ├── BuyStockDialog.java      Screen 5: Buy Stock
    │   │   ├── SellStockDialog.java     Screen 6: Sell Stock
    │   │   ├── PortfolioDialog.java     Screen 7: Portfolio
    │   │   ├── TransactionHistoryDialog.java  Screen 8: Transaction History
    │   │   ├── PerformanceDialog.java   Screen 9: Portfolio Performance
    │   │   └── SimpleDocumentListener.java     small Swing helper
    │   │
    │   └── util/                        Small helpers
    │       ├── CsvUtil.java             generic CSV read/write helper
    │       └── CurrencyFormatter.java   currency/percent string formatting
    │
    └── test/java/com/stocktrading/model/
        └── HoldingTest.java             JUnit 5 tests for Holding calculations
```

## Data flow (BUY example)

```
BuyStockDialog (UI)
      │  user clicks "Confirm BUY"
      ▼
TradingService.buy(user, symbol, quantity)
      │
      ├─► MarketService.getStock(symbol)          -- validates stock exists, gets price
      ├─► validates quantity > 0
      ├─► validates user.getBalance() >= totalCost
      ├─► user.debit(totalCost) + UserService.persist(user)
      ├─► PortfolioService.applyBuy(...)           -- updates/creates Holding
      └─► TransactionService.recordBuy(...)        -- appends a BuyTransaction record
```

SELL follows the same shape via `TradingService.sell(...)`.
