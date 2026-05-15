package com.catering.tests;

import com.catering.model.EventType;
import com.catering.patterns.builder.CateringMenuBuilder;
import com.catering.patterns.facade.OrderFacade;
import com.catering.patterns.factory.EventFactory;
import com.catering.service.AuthService;
import com.catering.service.PaymentService;
import com.catering.service.ValidationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionalFlowTest {
    @Test
    void runsFullFlowLoginToTracking() {
        AuthService authService = new AuthService();
        assertTrue(authService.login("admin@catering.com", "secret1"));

        var event = new EventFactory().createEvent(EventType.BIRTHDAY);
        event.setGuestCount(30);
        event.setLocation("Downtown Hall");
        event.setDate(LocalDate.now().plusDays(3));

        var menu = new CateringMenuBuilder()
                .addMainDish("Pasta")
                .addDrink("Juice")
                .addDessert("Brownie")
                .buildMenu();

        OrderFacade facade = new OrderFacade(new ValidationService(), new PaymentService());
        var order = facade.confirmOrder(event, menu, true, true, "Wallet");
        assertNotNull(order.getConfirmationCode());
        assertEquals("Wallet", order.getPaymentMethod());
    }
}
