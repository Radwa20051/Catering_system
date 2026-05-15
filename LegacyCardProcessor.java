package com.catering.patterns.adapter;

public class LegacyCardProcessor {
    public boolean charge(double value) {
        return value > 0 && value <= 100_000;
    }
}
