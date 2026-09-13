-- ============================================================================
-- Stock Trading Platform - Reference PostgreSQL Schema
-- ============================================================================
-- NOTE: The application, as shipped, uses simple FILE-BASED persistence
-- (CSV files under ./data) so that it can be imported and run immediately
-- without installing or configuring a database server. This script is
-- provided as a reference/optional schema that mirrors the exact data model
-- used by the file repositories (see com.stocktrading.repository), for
-- anyone who wants to extend the project to use PostgreSQL + JDBC instead.
-- It is NOT executed automatically and is not required to run the app.
-- ============================================================================

CREATE DATABASE stock_trading_platform;

-- Connect to the database before running the statements below:
-- \c stock_trading_platform

CREATE TABLE users (
    id                SERIAL PRIMARY KEY,
    username          VARCHAR(50) UNIQUE NOT NULL,
    password          VARCHAR(100) NOT NULL,
    balance           NUMERIC(15, 2) NOT NULL,
    starting_balance  NUMERIC(15, 2) NOT NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE stocks (
    symbol            VARCHAR(20) PRIMARY KEY,
    company_name      VARCHAR(150) NOT NULL,
    current_price     NUMERIC(15, 2) NOT NULL,
    previous_price    NUMERIC(15, 2) NOT NULL
);

CREATE TABLE holdings (
    id                SERIAL PRIMARY KEY,
    user_id           INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    symbol            VARCHAR(20) NOT NULL REFERENCES stocks(symbol),
    quantity          INTEGER NOT NULL CHECK (quantity >= 0),
    average_buy_price NUMERIC(15, 2) NOT NULL,
    UNIQUE (user_id, symbol)
);

CREATE TABLE transactions (
    id                SERIAL PRIMARY KEY,
    user_id           INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    symbol            VARCHAR(20) NOT NULL REFERENCES stocks(symbol),
    type              VARCHAR(4) NOT NULL CHECK (type IN ('BUY', 'SELL')),
    quantity          INTEGER NOT NULL CHECK (quantity > 0),
    price             NUMERIC(15, 2) NOT NULL,
    total_amount      NUMERIC(15, 2) GENERATED ALWAYS AS (quantity * price) STORED,
    transaction_time  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE portfolio_performance (
    id                SERIAL PRIMARY KEY,
    user_id           INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    snapshot_time     TIMESTAMP NOT NULL DEFAULT NOW(),
    portfolio_value   NUMERIC(15, 2) NOT NULL,
    cash_balance      NUMERIC(15, 2) NOT NULL,
    total_value       NUMERIC(15, 2) NOT NULL
);

-- ----------------------------------------------------------------------------
-- Sample data (matches the demo data seeded by the file-based version)
-- ----------------------------------------------------------------------------

INSERT INTO users (username, password, balance, starting_balance) VALUES
    ('demo', 'demo123', 100000.00, 100000.00);

INSERT INTO stocks (symbol, company_name, current_price, previous_price) VALUES
    ('TCS',      'Tata Consultancy Services', 3500.00, 3500.00),
    ('INFY',     'Infosys Ltd.',              1600.00, 1600.00),
    ('RELIANCE', 'Reliance Industries Ltd.',  2900.00, 2900.00),
    ('HDFCBANK', 'HDFC Bank Ltd.',            1700.00, 1700.00),
    ('WIPRO',    'Wipro Ltd.',                 500.00,  500.00),
    ('ITC',      'ITC Ltd.',                   450.00,  450.00);
