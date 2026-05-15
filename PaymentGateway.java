package com.catering.patterns.adapter;

public interface PaymentGateway {
    boolean pay(double amount);
}
