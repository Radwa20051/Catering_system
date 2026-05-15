package com.catering.patterns.singleton;

import com.catering.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CateringManager {
    private static final CateringManager INSTANCE = new CateringManager();
    private final List<Order> orders = new ArrayList<>();

    private CateringManager() {
    }

    public static CateringManager getInstance() {
        return INSTANCE;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> viewOrders() {
        return Collections.unmodifiableList(orders);
    }
}
