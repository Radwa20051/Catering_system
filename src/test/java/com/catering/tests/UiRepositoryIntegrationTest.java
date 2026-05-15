package com.catering.tests;

import com.catering.ui.customer.Customer;
import com.catering.ui.customer.CustomerRepository;
import com.catering.ui.notifications.AppNotificationRepository;
import com.catering.ui.order.Order;
import com.catering.ui.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiRepositoryIntegrationTest {

    @BeforeEach
    void resetStores() {
        OrderRepository.resetForTests();
        CustomerRepository.resetForTests();
        AppNotificationRepository.clear();
    }

    @Test
    void addOrderAndStatusChangeEmitInboxEntries() {
        Order o = new Order();
        o.setCustomer(new Customer("Alex", "555", "alex@example.com"));
        o.setEventType("Corporate");
        o.setEventDate(LocalDate.now().plusDays(2));
        o.setStatus("Pending");
        OrderRepository.addOrder(o);
        assertEquals("ORD-1001", o.getOrderId());
        assertEquals(1, AppNotificationRepository.getEntriesNewestFirst().size());

        OrderRepository.updateOrderStatus(o, "Preparing");
        assertEquals(2, AppNotificationRepository.getEntriesNewestFirst().size());
        assertEquals("Preparing", o.getStatus());

        OrderRepository.updateOrderStatus(o, "Preparing");
        assertEquals(2, AppNotificationRepository.getEntriesNewestFirst().size());
    }

    @Test
    void normalizeStatusUsesCanonicalLabels() {
        assertEquals("Pending", OrderRepository.normalizeStatus("pending"));
        assertEquals("Cancelled", OrderRepository.normalizeStatus("cancelled"));
    }

    @Test
    void terminalStatusesRecognized() {
        assertTrue(OrderRepository.isTerminalStatus("Delivered"));
        assertTrue(OrderRepository.isTerminalStatus("cancelled"));
        assertTrue(!OrderRepository.isTerminalStatus("Preparing"));
    }

    @Test
    void addOrderRejectsCustomerWithoutIdentity() {
        Order bad = new Order();
        bad.setCustomer(new Customer("", "", ""));
        bad.setEventType("Wedding");
        assertThrows(IllegalArgumentException.class, () -> OrderRepository.addOrder(bad));
    }

    @Test
    void addOrderRejectsMissingEventType() {
        Order bad = new Order();
        bad.setCustomer(new Customer("A", "1", "a@a.com"));
        assertThrows(IllegalArgumentException.class, () -> OrderRepository.addOrder(bad));
    }
}
