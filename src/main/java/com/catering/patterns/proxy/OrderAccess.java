package com.catering.patterns.proxy;

import com.catering.ui.order.Order;

public interface OrderAccess {
    boolean updateOrderStatus(Order order, String newStatus);

    boolean cancelOrder(Order order);

    boolean modifyTracking(Order order, String note);
}

