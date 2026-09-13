package com.stocktrading.model;

import java.time.LocalDateTime;

/**
 * Represents a registered user of the trading platform.
 * Demonstrates encapsulation: fields are private with controlled access via getters/setters.
 */
public class User {

    private final int id;
    private final String username;
    private String password;
    private double balance;
    private final double startingBalance;
    private final LocalDateTime createdAt;

    public User(int id, String username, String password, double balance, double startingBalance, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.startingBalance = startingBalance;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Deducts the given amount from the user's cash balance.
     */
    public void debit(double amount) {
        this.balance -= amount;
    }

    /**
     * Adds the given amount to the user's cash balance.
     */
    public void credit(double amount) {
        this.balance += amount;
    }

    @Override
    public String toString() {
        return username + " (Balance: " + String.format("%.2f", balance) + ")";
    }
}
