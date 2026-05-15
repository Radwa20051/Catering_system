package com.catering.tests;

import com.catering.model.EventType;
import com.catering.patterns.facade.OrderFacade;
import com.catering.patterns.factory.EventFactory;
import com.catering.service.PaymentService;
import com.catering.service.ValidationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacadePatternTest {
    @Test
    void calculatesTotalFromSingleApi() {
        var event = new EventFactory().createEvent(EventType.MEETING);
        event.setGuestCount(20);
        event.setLocation("HQ");
        event.setDate(LocalDate.now().plusDays(2));
        var facade = new OrderFacade(new ValidationService(), new PaymentService());
        assertTrue(facade.calculateTotal(event, true, true) > 0);
    }

    @Test
    void wizardCartPricingMatchesWizardFormula() {
        var event = new EventFactory().createEvent(EventType.MEETING); // base=1200, perGuest=15, multiplier=1.0
        var facade = new OrderFacade(new ValidationService(), new PaymentService());
        
        // (Menu 10 + PerGuest 15 + Dessert 12 + Drinks 8) * 2 guests + Base 1200 = (45 * 2) + 1200 = 1290.0
        assertEquals(1290.0, facade.calculateWizardCart(event, 10.0, 2, true, true), 0.001);
        
        // (Menu 10 + PerGuest 15 + Dessert 12 + Drinks 8) * 0 guests + Base 1200 = 1200.0 (multiplier 1.0)
        assertEquals(1200.0, facade.calculateWizardCart(event, 10.0, 0, true, true), 0.001);
    }
}
