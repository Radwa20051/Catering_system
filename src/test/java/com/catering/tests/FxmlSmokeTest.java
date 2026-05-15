package com.catering.tests;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FxmlSmokeTest {

    private static final AtomicBoolean FX_STARTED = new AtomicBoolean(false);

    @BeforeAll
    static void startJavaFx() throws Exception {
        if (FX_STARTED.compareAndSet(false, true)) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
            } catch (IllegalStateException alreadyStarted) {
                latch.countDown();
            }
            assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX platform did not start");
        }
    }

    @Test
    void loadsAllViews() throws Exception {
        List<String> views = List.of(
                "/com/catering/ui/main/main-shell.fxml",
                "/com/catering/ui/dashboard/dashboard-view.fxml",
                "/com/catering/ui/order/new-order-wizard.fxml",
                "/com/catering/ui/orders/orders-view.fxml",
                "/com/catering/ui/customers/customers-view.fxml",
                "/com/catering/ui/tracking/tracking-view.fxml",
                "/com/catering/ui/notifications/notifications-view.fxml",
                "/com/catering/ui/settings/settings-view.fxml",
                "/com/catering/ui/calendar/calendar-view.fxml"
        );

        for (String view : views) {
            var url = FxmlSmokeTest.class.getResource(view);
            assertNotNull(url, "Missing FXML resource: " + view);
            Object root = new FXMLLoader(url).load();
            assertNotNull(root, "Failed to load: " + view);
        }
    }
}

