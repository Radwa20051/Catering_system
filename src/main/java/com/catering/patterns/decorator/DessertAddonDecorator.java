package com.catering.patterns.decorator;

public class DessertAddonDecorator extends AddonDecorator {
    public DessertAddonDecorator(OrderComponent component) { super(component); }
    @Override public double getCost() { return component.getCost() + 15.0; }
}
