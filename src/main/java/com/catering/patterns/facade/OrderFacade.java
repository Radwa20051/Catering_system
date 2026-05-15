package com.catering.patterns.facade;

import com.catering.model.Event;
import com.catering.patterns.decorator.*;
import com.catering.service.PaymentService;
import com.catering.service.ValidationService;

public class OrderFacade {
    private final ValidationService validationService;
    private final PaymentService paymentService;

    public OrderFacade(ValidationService validationService, PaymentService paymentService) {
        this.validationService = validationService;
        this.paymentService = paymentService;
    }

    public double calculateTotal(Event event, boolean hasDessert, boolean hasDrinks) {
        validationService.validateEvent(event);
        String type = event.getClass().getSimpleName().replace("Event", "");
        double base = paymentService.getBaseRate(type);
        
        OrderComponent order = new BaseOrderComponent(base);
        if (hasDessert) order = new DessertAddonDecorator(order);
        if (hasDrinks) order = new DrinkAddonDecorator(order);
        
        return order.getCost();
    }

    public double calculateWizardCart(Event event, double menuCost, int guests, boolean hasDessert, boolean hasDrinks) {
        String type = event.getClass().getSimpleName().replace("Event", "");
        double base = paymentService.getBaseRate(type);
        
        double perGuest = 15.0; // Base per guest rate
        double totalPerGuest = menuCost + perGuest;
        if (hasDessert) totalPerGuest += 12.0;
        if (hasDrinks) totalPerGuest += 8.0;
        
        return base + (totalPerGuest * guests);
    }
}
