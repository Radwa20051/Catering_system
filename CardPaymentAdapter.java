package com.catering.patterns.adapter;

public class CardPaymentAdapter implements PaymentGateway {
    private final LegacyCardProcessor processor = new LegacyCardProcessor();

    @Override
    public boolean pay(double amount) {
        return processor.charge(amount);
    }
}
