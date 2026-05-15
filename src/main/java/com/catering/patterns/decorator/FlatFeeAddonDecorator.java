package com.catering.patterns.decorator;

public class FlatFeeAddonDecorator extends AddonDecorator {
    private final double fee;
    public FlatFeeAddonDecorator(OrderComponent component, double fee, String note) { super(component); this.fee = fee; }
    @Override public double getCost() { return component.getCost() + fee; }
}
