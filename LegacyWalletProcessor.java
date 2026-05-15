package com.catering.patterns.adapter;

public class LegacyWalletProcessor {
    public boolean processTransfer(String currency, double amount) {
        return "USD".equals(currency) && amount > 0;
    }
}
