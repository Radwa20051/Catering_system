package com.catering.ui.customers;

import com.catering.ui.customer.Customer;
import com.catering.ui.customer.CustomerRepository;
import com.catering.ui.order.Order;
import com.catering.ui.order.OrderRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomersController {

    @FXML private TableView<CustomerRow> customersTable;
    @FXML private TableColumn<CustomerRow, String> nameColumn;
    @FXML private TableColumn<CustomerRow, String> phoneColumn;
    @FXML private TableColumn<CustomerRow, String> ordersColumn;

    @FXML private TextField newNameField;
    @FXML private TextField newPhoneField;
    @FXML private TextField newEmailField;
    @FXML private Button addCustomerButton;

    @FXML
    public void initialize() {

        Label empty = new Label("No customers found.");

        empty.getStyleClass().add("content-header-sub");

        customersTable.setPlaceholder(empty);

        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        phoneColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        ordersColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrderCount()));
        if (customersTable != null) {
            customersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            customersTable.setFixedCellSize(44);
        }
        refresh();
    }

    public void refresh() {
        var rows = FXCollections.<CustomerRow>observableArrayList();
        for (Customer c : CustomerRepository.getAllCustomers()) {
            rows.add(new CustomerRow(
                    safeText(c.getName()),
                    safeText(c.getPhone()),
                    String.valueOf(countOrdersForCustomer(c))
            ));
        }
        if (customersTable != null) {
            customersTable.setItems(rows);
        }
    }

    @FXML
    public void onAddCustomer() {
        String name = newNameField != null ? newNameField.getText() : "";
        String phone = newPhoneField != null ? newPhoneField.getText() : "";
        String email = newEmailField != null ? newEmailField.getText() : "";
        CustomerRepository.addCustomer(new Customer(name, phone, email));
        if (newNameField != null) newNameField.clear();
        if (newPhoneField != null) newPhoneField.clear();
        if (newEmailField != null) newEmailField.clear();
        refresh();
    }

    private long countOrdersForCustomer(Customer c) {
        if (c == null) {
            return 0;
        }
        long count = 0;
        for (Order o : OrderRepository.getAllOrders()) {
            if (o.getCustomer() == null) {
                continue;
            }
            if (o.getCustomer() == c) {
                count++;
                continue;
            }
            if (samePhone(o.getCustomer(), c)) {
                count++;
                continue;
            }
            if (sameEmail(o.getCustomer(), c)) {
                count++;
            }
        }
        return count;
    }

    private static boolean samePhone(Customer a, Customer b) {
        if (a == null || b == null) {
            return false;
        }
        String ap = a.getPhone() == null ? "" : a.getPhone().trim();
        String bp = b.getPhone() == null ? "" : b.getPhone().trim();
        return !ap.isEmpty() && ap.equalsIgnoreCase(bp);
    }

    private static boolean sameEmail(Customer a, Customer b) {
        if (a == null || b == null) {
            return false;
        }
        String ae = a.getEmail() == null ? "" : a.getEmail().trim();
        String be = b.getEmail() == null ? "" : b.getEmail().trim();
        return !ae.isEmpty() && ae.equalsIgnoreCase(be);
    }

    private String safeText(String s) {
        if (s == null) {
            return "—";
        }
        String t = s.trim();
        return t.isEmpty() ? "—" : t;
    }

    public static class CustomerRow {
        private final String name;
        private final String phone;
        private final String orderCount;

        public CustomerRow(String name, String phone, String orderCount) {
            this.name = name;
            this.phone = phone;
            this.orderCount = orderCount;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getOrderCount() {
            return orderCount;
        }
    }
}
