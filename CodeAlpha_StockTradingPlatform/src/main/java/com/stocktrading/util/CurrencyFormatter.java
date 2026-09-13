package com.stocktrading.util;

/**
 * Small helper for formatting currency amounts consistently across the UI.
 */
public final class CurrencyFormatter {

    private CurrencyFormatter() {
        // utility class - no instances
    }

    public static String format(double amount) {
        return String.format("Rs. %,.2f", amount);
    }

    public static String formatPercent(double percent) {
        return String.format("%.2f%%", percent);
    }
}
