package com.catering.tests;

import com.catering.service.PaymentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AdapterPatternTest {
    @Test
    void processesPaymentThroughUnifiedInterface() {
        assertTrue(new PaymentService().processPayment("Card", 500));
    }
}
