package com.catering.patterns.proxy;

import com.catering.ui.notifications.AppNotificationRepository;
import com.catering.ui.order.Order;
import com.catering.ui.order.OrderRepository;

public class RealOrderService implements OrderAccess {
    @Override
    public boolean updateOrderStatus(Order order, String newStatus) {
        if (order == null || newStatus == null || newStatus.trim().isEmpty()) {
            return false;
        }
        OrderRepository.updateOrderStatus(order, newStatus);
        return true;
    }

    @Override
    public boolean cancelOrder(Order order) {
        if (order == null) {
            return false;
        }
        OrderRepository.updateOrderStatus(order, "Cancelled");
        return true;
    }

    @Override
    public boolean modifyTracking(Order order, String note) {
        if (order == null || note == null || note.trim().isEmpty()) {
            return false;
        }
        AppNotificationRepository.addEntry("Tracking note: " + order.getOrderId() + " — " + note.trim());
        OrderRepository.notifyOrdersChanged();
        return true;
    }
}

