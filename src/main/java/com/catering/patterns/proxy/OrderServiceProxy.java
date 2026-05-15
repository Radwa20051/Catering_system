package com.catering.patterns.proxy;

import com.catering.ui.notifications.AppNotificationRepository;
import com.catering.ui.order.Order;

import java.util.Objects;

public class OrderServiceProxy implements OrderAccess {
    private final OrderAccess real;
    private final boolean isAdmin;

    public OrderServiceProxy(OrderAccess real, boolean isAdmin) {
        this.real = Objects.requireNonNull(real, "real");
        this.isAdmin = isAdmin;
    }

    @Override
    public boolean updateOrderStatus(Order order, String newStatus) {
        if (!isAdmin) {
            deny("update order status");
            return false;
        }
        return real.updateOrderStatus(order, newStatus);
    }

    @Override
    public boolean cancelOrder(Order order) {
        if (!isAdmin) {
            deny("cancel orders");
            return false;
        }
        return real.cancelOrder(order);
    }

    @Override
    public boolean modifyTracking(Order order, String note) {
        if (!isAdmin) {
            deny("modify tracking");
            return false;
        }
        return real.modifyTracking(order, note);
    }

    private static void deny(String action) {
        AppNotificationRepository.addEntry("Access denied: Admin required to " + action + ".");
    }
}

