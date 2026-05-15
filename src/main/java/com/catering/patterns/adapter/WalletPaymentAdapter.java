package com.catering.patterns.adapter;

public class WalletPaymentAdapter implements PaymentGateway {
    private final LegacyWalletProcessor processor = new LegacyWalletProcessor();

    @Override
    public boolean pay(double amount) {
        return processor.processTransfer("USD", amount);
    }
}
