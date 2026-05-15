package com.catering.patterns.decorator;

public class DrinkAddonDecorator extends AddonDecorator {
    public DrinkAddonDecorator(OrderComponent component) { super(component); }
    @Override public double getCost() { return component.getCost() + 10.0; }
}
