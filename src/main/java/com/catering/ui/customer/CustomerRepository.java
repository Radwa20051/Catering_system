package com.catering.ui.customer;

import com.catering.service.AppDataStore;

import java.util.List;

/**
 * Facade over {@link AppDataStore} for the shared customer directory.
 */
public final class CustomerRepository {
    private CustomerRepository() {}

    public static void addListener(Runnable r) {
        if (r == null) {
            return;
        }
        AppDataStore.getInstance().addCustomerListener(r);
    }

    public static void removeListener(Runnable r) {
        AppDataStore.getInstance().removeCustomerListener(r);
    }

    public static void addCustomer(Customer customer) {
        AppDataStore.getInstance().addCustomer(customer);
    }

    public static List<Customer> getAllCustomers() {
        return AppDataStore.getInstance().getCustomersSnapshot();
    }

    /** Clears all orders and customers — intended for tests only. */
    public static void resetForTests() {
        AppDataStore.getInstance().resetForTests();
    }
}
