package com.catering.ui.orders;

import com.catering.patterns.proxy.OrderAccess;
import com.catering.patterns.proxy.OrderServiceProxy;
import com.catering.patterns.proxy.RealOrderService;
import com.catering.service.AppSettings;
import com.catering.service.AppDataStore;
import com.catering.ui.customer.Customer;
import com.catering.ui.order.Order;
import com.catering.ui.order.OrderRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;

public class OrdersController {

    @FXML private TextField customerSearchField;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, String> customerColumn;
    @FXML private TableColumn<Order, String> eventColumn;
    @FXML private TableColumn<Order, String> eventDateColumn;
    @FXML private TableColumn<Order, String> totalPriceColumn;
    @FXML private TableColumn<Order, String> itemsCountColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> actionsColumn;

    private FilteredList<Order> filteredOrders;

    @FXML
    public void initialize() {

        Label empty = new Label("No orders available yet.");

        empty.getStyleClass().add("content-header-sub");

        ordersTable.setPlaceholder(empty);

        AppDataStore.getInstance().ensureOrderIds();
        ObservableList<Order> source = AppDataStore.getInstance().getOrdersObservable();
        filteredOrders = new FilteredList<>(source, o -> true);
        if (ordersTable != null) {
            ordersTable.setItems(filteredOrders);
            ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            ordersTable.setFixedCellSize(44);
        }

        if (orderIdColumn != null) {
            orderIdColumn.setCellValueFactory(cd -> {
                Order o = cd.getValue();
                String id = o == null ? null : o.getOrderId();
                return new SimpleStringProperty(safeText(id));
            });
        }
        if (customerColumn != null) {
            customerColumn.setCellValueFactory(cd -> new SimpleStringProperty(
                    customerLabel(cd.getValue() != null ? cd.getValue().getCustomer() : null)));
        }
        if (eventColumn != null) {
            eventColumn.setCellValueFactory(cd -> new SimpleStringProperty(
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
        if (totalPriceColumn != null) {
            totalPriceColumn.setCellValueFactory(cd -> {
                Order o = cd.getValue();
                return new SimpleStringProperty(safeText(o == null ? null : o.getEstimatedTotal()));
            });
            totalPriceColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        }
        if (itemsCountColumn != null) {
            itemsCountColumn.setCellValueFactory(cd -> {
                Order o = cd.getValue();
                return new SimpleStringProperty(o == null ? "—" : String.valueOf(itemsCount(o)));
            });
            itemsCountColumn.setStyle("-fx-alignment: CENTER;");
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cd -> {
                Order o = cd.getValue();
                String s = o == null ? "" : OrderRepository.normalizeStatus(o.getStatus());
                return new SimpleStringProperty(s);
            });
            statusColumn.setCellFactory(col -> new StatusComboTableCell());
        }
        if (actionsColumn != null) {
            actionsColumn.setCellValueFactory(cd -> new SimpleStringProperty(""));
            actionsColumn.setCellFactory(col -> new ViewDetailsCell());
            actionsColumn.setStyle("-fx-alignment: CENTER;");
        }

        if (customerSearchField != null) {
            customerSearchField.textProperty().addListener((obs, prev, text) -> applyCustomerFilter(text));
        }
        refresh();
    }

    public void refresh() {
        AppDataStore.getInstance().ensureOrderIds();
        applyCustomerFilter(customerSearchField != null ? customerSearchField.getText() : "");
        if (ordersTable != null) {
            ordersTable.refresh();
        }
    }

    private void applyCustomerFilter(String raw) {
        if (filteredOrders == null) {
            return;
        }
        String needle = raw == null ? "" : raw.trim().toLowerCase();
        if (needle.isEmpty()) {
            filteredOrders.setPredicate(o -> true);
            return;
        }
        filteredOrders.setPredicate(order -> {
            if (order == null || order.getCustomer() == null) {
                return false;
            }
            String name = order.getCustomer().getName();
            String hay = name == null ? "" : name.toLowerCase();
            return hay.contains(needle);
        });
    }

    private static String safeText(String s) {
        if (s == null) {
            return "—";
        }
        String t = s.trim();
        return t.isEmpty() ? "—" : t;
    }

    private static int itemsCount(Order o) {
        if (o == null) {
            return 0;
        }
        int c = 0;
        if (!"—".equals(safeText(o.getMainDish()))) c++;
        if (!"—".equals(safeText(o.getDrink()))) c++;
        if (!"—".equals(safeText(o.getDessert()))) c++;
        if (o.isDessertAddon()) c++;
        if (o.isDrinksAddon()) c++;
        return c;
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

    private static final class ViewDetailsCell extends TableCell<Order, String> {
        private final Button viewBtn = new Button("View Details");
        private final HBox box = new HBox(viewBtn);

        ViewDetailsCell() {
            box.setAlignment(Pos.CENTER);
            viewBtn.getStyleClass().add("view-details-btn");
            viewBtn.setPadding(new Insets(6, 12, 6, 12));
            viewBtn.setOnAction(e -> {
                Order o = getTableRow() != null ? getTableRow().getItem() : null;
                if (o != null) {
                    showDetails(o);
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
            setGraphic(box);
        }
    }

    private static void showDetails(Order o) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Order Details");
        URL css = OrdersController.class.getResource("/com/catering/styles/app.css");
        if (css != null) {
            dialog.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
        dialog.getDialogPane().getStyleClass().add("orders-details-dialog");
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setMinWidth(560);

        Label heading = new Label(safeText(o.getOrderId()) + " • " + safeText(o.getEventType()));
        heading.getStyleClass().add("wizard-title");

        HBox meta = new HBox(18,
                metaLine("Customer", customerLabel(o.getCustomer())),
                metaLine("Event date", o.getEventDate() == null ? "—" : o.getEventDate().toString()),
                metaLine("Total", safeText(o.getEstimatedTotal()))
        );
        meta.setAlignment(Pos.CENTER_LEFT);

        VBox menuBox = new VBox(10);
        menuBox.getStyleClass().add("card-surface");
        menuBox.setPadding(new Insets(14));
        Label menuTitle = new Label("Selected items");
        menuTitle.getStyleClass().add("field-label");
        menuBox.getChildren().add(menuTitle);

        int qty = Math.max(1, o.getGuestCount());
        menuBox.getChildren().add(section("Main dishes", itemRow(safeText(o.getMainDish()), qty)));
        menuBox.getChildren().add(section("Drinks", itemRow(safeText(o.getDrink()), qty)));
        menuBox.getChildren().add(section("Desserts", itemRow(safeText(o.getDessert()), qty)));
        menuBox.getChildren().add(section("Add-ons",
                addonRow("Extra desserts", o.isDessertAddon()),
                addonRow("Extra drinks", o.isDrinksAddon())
        ));

        VBox notesBox = new VBox(8);
        notesBox.getStyleClass().add("card-surface");
        notesBox.setPadding(new Insets(14));
        Label notesTitle = new Label("Notes");
        notesTitle.getStyleClass().add("field-label");
        Label notes = new Label("Location: " + safeText(o.getLocation()) + "\nPayment: " + safeText(o.getPaymentMethod()));
        notes.setWrapText(true);
        notesBox.getChildren().addAll(notesTitle, notes);

        VBox statusBox = new VBox(8);
        statusBox.getStyleClass().add("card-surface");
        statusBox.setPadding(new Insets(14));
        Label statusTitle = new Label("Status");
        statusTitle.getStyleClass().add("field-label");
        Label badge = new Label(OrderRepository.normalizeStatus(o.getStatus()));
        badge.getStyleClass().addAll("status-pill");
        applyStatusStyle(badge, badge.getText());
        statusBox.getChildren().addAll(statusTitle, badge);

        root.getChildren().addAll(heading, meta, menuBox, notesBox, statusBox);
        dialog.getDialogPane().setContent(root);
        dialog.showAndWait();
    }

    private static VBox metaLine(String k, String v) {
        VBox box = new VBox(2);
        Label key = new Label(k);
        key.getStyleClass().add("field-label");
        Label val = new Label(safeText(v));
        val.getStyleClass().add("meta-value");
        box.getChildren().addAll(key, val);
        return box;
    }

    private static VBox section(String title, HBox... rows) {
        VBox box = new VBox(6);
        Label t = new Label(title);
        t.getStyleClass().add("section-title");
        box.getChildren().add(t);
        for (HBox r : rows) {
            if (r != null) {
                box.getChildren().add(r);
            }
        }
        return box;
    }

    private static HBox itemRow(String name, int qty) {
        if ("—".equals(name)) {
            return itemRow("—", 0, true);
        }
        return itemRow(name, qty, false);
    }

    private static HBox itemRow(String name, int qty, boolean muted) {
        Label n = new Label(name);
        n.getStyleClass().add(muted ? "muted" : "item-name");
        Label q = new Label(muted ? "" : ("x" + qty));
        q.getStyleClass().add("item-qty");
        HBox row = new HBox(10, n, new javafx.scene.layout.Region(), q);
        HBox.setHgrow(row.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static HBox addonRow(String label, boolean enabled) {
        return itemRow(enabled ? label : "—", enabled ? 1 : 0, !enabled);
    }

    private static boolean isAdmin() {
        return AppSettings.role != null && AppSettings.role.trim().equalsIgnoreCase("Admin");
    }
}
