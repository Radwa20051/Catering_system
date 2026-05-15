package com.catering.patterns.decorator;

public class BaseOrderComponent implements OrderComponent {
    private final double cost;
    public BaseOrderComponent(double cost) { this.cost = cost; }
    @Override public double getCost() { return cost; }
}
