package com.catering.patterns.decorator;

public abstract class AddonDecorator implements OrderComponent {
    protected final OrderComponent component;
    public AddonDecorator(OrderComponent component) { this.component = component; }
}
