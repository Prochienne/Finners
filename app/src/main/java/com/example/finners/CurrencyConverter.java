package com.example.finners;

import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    private static final Map<String, Double> exchangeRates = new HashMap<>();

    static {
        // Base currency: USD
        exchangeRates.put("USD", 1.0);
        exchangeRates.put("EUR", 0.95);
        exchangeRates.put("GBP", 0.79);
        exchangeRates.put("JPY", 149.5);
        exchangeRates.put("AUD", 1.54);
        exchangeRates.put("CAD", 1.40);
        exchangeRates.put("CHF", 0.88);
        exchangeRates.put("CNY", 7.25);
        exchangeRates.put("INR", 84.5);
        exchangeRates.put("AED", 3.67);
    }

    public static double convert(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        Double fromRate = exchangeRates.get(fromCurrency);
        Double toRate = exchangeRates.get(toCurrency);

        if (fromRate == null || toRate == null) {
            // Fallback: 1:1 conversion if rate not found
            return amount;
        }

        // Convert to USD first, then to target currency
        double amountInUsd = amount / fromRate;
        return amountInUsd * toRate;
    }
    
    public static boolean isSupported(String currencyCode) {
        return exchangeRates.containsKey(currencyCode);
    }
}
