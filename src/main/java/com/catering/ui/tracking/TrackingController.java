package com.catering.ui.tracking;

import com.catering.patterns.proxy.OrderAccess;
import com.catering.patterns.proxy.OrderServiceProxy;
import com.catering.patterns.proxy.RealOrderService;
import com.catering.service.AppDataStore;
import com.catering.service.AppSettings;
import com.catering.ui.customer.Customer;
import com.catering.ui.notifications.AppNotificationRepository;
import com.catering.ui.order.Order;
import com.catering.ui.order.OrderRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class TrackingController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, String> customerColumn;
    @FXML private TableColumn<Order, String> eventTypeColumn;
    @FXML private TableColumn<Order, String> eventDateColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TextArea activityArea;

    @FXML
    public void initialize() {

        Label empty = new Label("No active orders to track.");

        empty.getStyleClass().add("content-header-sub");

        ordersTable.setPlaceholder(empty);

        AppDataStore.getInstance().ensureOrderIds();
        if (orderIdColumn != null) {
            orderIdColumn.setCellValueFactory(cd -> {
                Order o = cd.getValue();
                if (o == null) {
                    return new SimpleStringProperty("—");
                }
                if (o.getOrderId() == null || o.getOrderId().trim().isEmpty()) {
                    AppDataStore.getInstance().ensureOrderIds();
                }
                String id = o.getOrderId();
                String t = id == null ? "" : id.trim();
                return new SimpleStringProperty(t.isEmpty() ? "—" : t);
            });
        }
        if (customerColumn != null) {
            customerColumn.setCellValueFactory(cd -> new SimpleStringProperty(
                    customerLabel(cd.getValue() != null ? cd.getValue().getCustomer() : null)));
        }
        if (eventTypeColumn != null) {
            eventTypeColumn.setCellValueFactory(cd -> new SimpleStringProperty(
                    safeText(cd.getValue() != null ? cd.getValue().getEventType() : null)));
        }
        if (eventDateColumn != null) {
            eventDateColumn.setCellValueFactory(cd -> {
                Order o = cd.getValue();
                if (o == null || o.getEventDate() == null) {
                    return new SimpleStringProperty("—");
                }
                return new SimpleStringProperty(o.getEventDate().toString());
            });
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cd -> {
                Order o = cd.getValue();
                String s = o == null ? "" : OrderRepository.normalizeStatus(o.getStatus());
                return new SimpleStringProperty(s);
            });
            statusColumn.setCellFactory(col -> new StatusComboTableCell());
        }
        if (ordersTable != null) {
            ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            ordersTable.setFixedCellSize(44);
        }
        refresh();
    }

    public void refresh() {
        AppDataStore.getInstance().ensureOrderIds();
        if (ordersTable != null) {
            ordersTable.setItems(FXCollections.observableArrayList(OrderRepository.getAllOrders()));
            ordersTable.refresh();
        }
        if (activityArea != null) {
            activityArea.setText(AppNotificationRepository.getEntriesNewestFirst().stream()
                    .map(AppNotificationRepository.AppNotification::formatLine)
                    .collect(Collectors.joining(System.lineSeparator())));
        }
    }

    private static String safeText(String s) {
        if (s == null) {
            return "—";
        }
        String t = s.trim();
        return t.isEmpty() ? "—" : t;
    }

    private static String customerLabel(Customer c) {
        if (c == null) {
            return "—";
        }
        String n = c.getName() == null ? "" : c.getName().trim();
        if (!n.isEmpty()) {
            return n;
        }
        String p = c.getPhone() == null ? "" : c.getPhone().trim();
        if (!p.isEmpty()) {
            return p;
        }
        String e = c.getEmail() == null ? "" : c.getEmail().trim();
        return e.isEmpty() ? "—" : e;
    }

    private static final class StatusComboTableCell extends TableCell<Order, String> {
        private final ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList("Pending", "Preparing", "Delivered", "Cancelled"));
        private boolean suppressComboAction;
        private final OrderAccess access = new OrderServiceProxy(new RealOrderService(), isAdmin());

        StatusComboTableCell() {
            combo.setMaxWidth(Double.MAX_VALUE);
            combo.getStyleClass().add("status-badge");
            combo.valueProperty().addListener((obs, prev, v) -> applyStatusStyle(combo, v));
            combo.setOnAction(e -> {
                if (suppressComboAction) {
                    return;
                }
                Order row = getTableRow() != null ? getTableRow().getItem() : null;
                if (row != null && combo.getValue() != null) {
                    boolean ok = access.updateOrderStatus(row, combo.getValue());
                    if (!ok) {
                        suppressComboAction = true;
                        try {
                            combo.setValue(OrderRepository.normalizeStatus(row.getStatus()));
                        } finally {
                            suppressComboAction = false;
                        }
                    }
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setGraphic(null);
                return;
            }
            Order o = getTableRow().getItem();
            suppressComboAction = true;
            try {
                combo.setValue(OrderRepository.normalizeStatus(o.getStatus()));
            } finally {
                suppressComboAction = false;
            }
            applyStatusStyle(combo, combo.getValue());
            setGraphic(combo);
        }
    }

    private static void applyStatusStyle(Control control, String status) {
        if (control == null) {
            return;
        }
        control.getStyleClass().removeAll("badge-pending", "badge-preparing", "badge-delivered", "badge-cancelled");
        String s = OrderRepository.normalizeStatus(status);
        if ("Pending".equalsIgnoreCase(s)) {
            control.getStyleClass().add("badge-pending");
        } else if ("Preparing".equalsIgnoreCase(s)) {
            control.getStyleClass().add("badge-preparing");
        } else if ("Delivered".equalsIgnoreCase(s)) {
            control.getStyleClass().add("badge-delivered");
        } else if ("Cancelled".equalsIgnoreCase(s)) {
            control.getStyleClass().add("badge-cancelled");
        }
    }

    private static boolean isAdmin() {
        return AppSettings.role != null && AppSettings.role.trim().equalsIgnoreCase("Admin");
    }
}
