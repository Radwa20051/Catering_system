package com.catering.service;

import com.catering.patterns.adapter.CardPaymentAdapter;
import com.catering.patterns.adapter.PaymentGateway;
import com.catering.patterns.adapter.WalletPaymentAdapter;

public class PaymentService {
    public boolean processPayment(String method, double amount) {
        PaymentGateway gateway;
        if ("Card".equalsIgnoreCase(method)) {
            gateway = new CardPaymentAdapter();
        } else {
            gateway = new WalletPaymentAdapter();
        }
        return gateway.pay(amount);
    }

    public double getBaseRate(String eventType) {
        if (eventType == null) return 0.0;
        return switch (eventType.toUpperCase()) {
            case "WEDDING" -> 2000.0;
            case "MEETING" -> 1200.0;
            case "BIRTHDAY" -> 800.0;
            default -> 1000.0;
        };
    }
}
