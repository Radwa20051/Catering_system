package com.catering.ui.dashboard;

import com.catering.ui.order.Order;
import com.catering.ui.order.OrderRepository;
import com.catering.service.AppDataStore;
import com.catering.patterns.singleton.CateringManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML private Label activeOrdersValue;
    @FXML private Label upcomingEventsValue;
    @FXML private Label pendingPaymentsValue;
    @FXML private Label openWorkflowsValue;

    @FXML private TableView<Order> recentOrdersTable;

    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, String> customerColumn;
    @FXML private TableColumn<Order, String> eventColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> priceColumn;

    @FXML private PieChart statusChart;
    @FXML private VBox upcomingEventsContainer;

    @FXML private Button viewAllOrdersButton;
    @FXML private Button viewCalendarButton;
    @FXML private Button createNewOrderButton;
    @FXML private Button openCalendarButton;
    @FXML private Button manageCustomersButton;
    @FXML private Button trackOrdersButton;

    private Runnable onOpenOrders;
    private Runnable onOpenCalendar;
    private Runnable onOpenNewOrder;
    private Runnable onOpenCustomers;
    private Runnable onOpenTracking;

    @FXML
    public void initialize() {
        setupRecentOrdersTable();
        refresh();
    }

    public void refresh() {
        AppDataStore.getInstance().ensureOrderIds();
        List<Order> orders = OrderRepository.getAllOrders();

        long active = orders.stream()
                .filter(DashboardController::isActiveOrder)
                .count();

        long upcoming = orders.stream()
                .filter(DashboardController::isUpcomingEvent)
                .count();

        long unpaid = orders.stream()
                .filter(o -> o != null && !o.isPaid())
                .count();

        long openWorkflows = orders.stream()
                .filter(DashboardController::isOpenWorkflow)
                .count();

        activeOrdersValue.setText(String.valueOf(active));
        upcomingEventsValue.setText(String.valueOf(upcoming));
        pendingPaymentsValue.setText(String.valueOf(unpaid));
        openWorkflowsValue.setText(String.valueOf(openWorkflows));

        refreshRecentOrders(orders);
        refreshUpcomingEvents(orders);
        refreshStatusChart(orders);
        injectSingletonAuditLabel();
    }

    private void injectSingletonAuditLabel() {
        if (upcomingEventsContainer == null) {
            return;
        }
        int domainCount = CateringManager.getInstance().viewOrders().size();
        Label auditLabel = new Label("Registered catering orders: " + domainCount );
        auditLabel.getStyleClass().add("event-meta");
        auditLabel.setPadding(new javafx.geometry.Insets(0, 0, 8, 4));
        upcomingEventsContainer.getChildren().add(0, auditLabel);
    }

    private void setupRecentOrdersTable() {
        orderIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        safeText(data.getValue() == null ? null : data.getValue().getOrderId())
                ));

        customerColumn.setCellValueFactory(data -> {
            String customerName = "";

            if (data.getValue() != null && data.getValue().getCustomer() != null) {
                customerName = data.getValue()
                        .getCustomer()
                        .getName();
            }

            return new SimpleStringProperty(safeText(customerName));
        });

        eventColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        safeText(data.getValue() == null ? null : data.getValue().getEventType())
                ));

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        OrderRepository.normalizeStatus(data.getValue() == null ? null : data.getValue().getStatus())
                ));
        statusColumn.setCellFactory(col -> new StatusBadgeCell());

        priceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        safeText(data.getValue() == null ? null : data.getValue().getEstimatedTotal())
                ));
    }

    private void refreshRecentOrders(List<Order> orders) {
        List<Order> recentOrders = orders.stream()
                .filter(o -> o != null)
                .sorted(
                        Comparator.comparingInt(DashboardController::parseOrderNumber).reversed()
                                .thenComparing(Comparator.comparing(DashboardController::sortDateFallback).reversed())
                )
                .limit(5)
                .toList();

        recentOrdersTable.getItems().setAll(recentOrders);
        recentOrdersTable.refresh();
    }

    private void refreshStatusChart(List<Order> orders) {
        long pending = orders.stream()
                .filter(o -> o != null && "Pending".equalsIgnoreCase(OrderRepository.normalizeStatus(o.getStatus())))
                .count();

        long preparing = orders.stream()
                .filter(o -> o != null && "Preparing".equalsIgnoreCase(OrderRepository.normalizeStatus(o.getStatus())))
                .count();

        long delivered = orders.stream()
                .filter(o -> o != null && "Delivered".equalsIgnoreCase(OrderRepository.normalizeStatus(o.getStatus())))
                .count();

        long cancelled = orders.stream()
                .filter(o -> o != null && "Cancelled".equalsIgnoreCase(OrderRepository.normalizeStatus(o.getStatus())))
                .count();

        statusChart.getData().clear();

        statusChart.getData().add(
                new PieChart.Data("Pending", pending)
        );

        statusChart.getData().add(
                new PieChart.Data("Preparing", preparing)
        );

        statusChart.getData().add(
                new PieChart.Data("Delivered", delivered)
        );

        statusChart.getData().add(
                new PieChart.Data("Cancelled", cancelled)
        );

        statusChart.setLegendVisible(true);
        statusChart.setLabelsVisible(true);
    }

    private void refreshUpcomingEvents(List<Order> orders) {
        if (upcomingEventsContainer == null) {
            return;
        }
        upcomingEventsContainer.getChildren().clear();

        List<Order> upcoming = orders.stream()
                .filter(DashboardController::isUpcomingEvent)
                .sorted(Comparator.comparing(Order::getEventDate))
                .limit(5)
                .toList();

        if (upcoming.isEmpty()) {
            Label empty = new Label("No upcoming events yet.");
            empty.getStyleClass().add("empty-state-text");
            upcomingEventsContainer.getChildren().add(empty);
            return;
        }

        for (Order order : upcoming) {
            upcomingEventsContainer.getChildren().add(buildUpcomingEventRow(order));
        }
    }

    private HBox buildUpcomingEventRow(Order order) {
        HBox row = new HBox(12);
        row.getStyleClass().add("event-row");

        VBox dateBadge = new VBox(2);
        dateBadge.getStyleClass().add("event-date-badge");
        Label month = new Label(formatMonth(order.getEventDate()));
        month.getStyleClass().add("event-date-month");
        Label day = new Label(formatDay(order.getEventDate()));
        day.getStyleClass().add("event-date-day");
        dateBadge.getChildren().addAll(month, day);

        VBox content = new VBox(4);
        Label title = new Label(buildEventTitle(order));
        title.getStyleClass().add("event-title");
        Label dateTime = new Label(formatEventDateTime(order));
        dateTime.getStyleClass().add("event-meta");
        content.getChildren().addAll(title, dateTime);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label status = new Label(OrderRepository.normalizeStatus(order.getStatus()));
        status.getStyleClass().add("status-mini-badge");
        status.getStyleClass().add(statusClass(order.getStatus()));

        row.getChildren().addAll(dateBadge, content, spacer, status);
        return row;
    }

    @FXML
    private void onOpenOrders() {
        runAction(onOpenOrders);
    }

    @FXML
    private void onOpenCalendar() {
        runAction(onOpenCalendar);
    }

    @FXML
    private void onOpenNewOrder() {
        runAction(onOpenNewOrder);
    }

    @FXML
    private void onOpenCustomers() {
        runAction(onOpenCustomers);
    }

    @FXML
    private void onOpenTracking() {
        runAction(onOpenTracking);
    }

    private void runAction(Runnable action) {
        if (action != null) {
            action.run();
        }
    }

    public void setNavigationActions(
            Runnable onOpenOrders,
            Runnable onOpenCalendar,
            Runnable onOpenNewOrder,
            Runnable onOpenCustomers,
            Runnable onOpenTracking
    ) {
        this.onOpenOrders = onOpenOrders;
        this.onOpenCalendar = onOpenCalendar;
        this.onOpenNewOrder = onOpenNewOrder;
        this.onOpenCustomers = onOpenCustomers;
        this.onOpenTracking = onOpenTracking;

        if (viewAllOrdersButton != null) {
            viewAllOrdersButton.setDisable(this.onOpenOrders == null);
        }
        if (viewCalendarButton != null) {
            viewCalendarButton.setDisable(this.onOpenCalendar == null);
        }
        if (createNewOrderButton != null) {
            createNewOrderButton.setDisable(this.onOpenNewOrder == null);
        }
        if (openCalendarButton != null) {
            openCalendarButton.setDisable(this.onOpenCalendar == null);
        }
        if (manageCustomersButton != null) {
            manageCustomersButton.setDisable(this.onOpenCustomers == null);
        }
        if (trackOrdersButton != null) {
            trackOrdersButton.setDisable(this.onOpenTracking == null);
        }
    }

    private static String formatMonth(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        return date.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH).toUpperCase(Locale.ENGLISH);
    }

    private static String formatDay(LocalDate date) {
        if (date == null) {
            return "--";
        }
        return String.format("%02d", date.getDayOfMonth());
    }

    private static String buildEventTitle(Order order) {
        String eventType = safeText(order == null ? null : order.getEventType());
        String customer = "-";
        if (order != null && order.getCustomer() != null) {
            customer = safeText(order.getCustomer().getName());
        }
        if ("-".equals(customer)) {
            return eventType;
        }
        return eventType + " - " + customer;
    }

    private static String formatEventDateTime(Order order) {
        if (order == null || order.getEventDate() == null) {
            return "Date not set";
        }
        LocalDate date = order.getEventDate();
        LocalDateTime dt = LocalDateTime.of(date, LocalTime.NOON);
        return dt.format(DateTimeFormatter.ofPattern("MMM d, yyyy - hh:mm a", Locale.ENGLISH));
    }

    private static LocalDate sortDateFallback(Order order) {
        if (order == null || order.getEventDate() == null) {
            return LocalDate.MIN;
        }
        return order.getEventDate();
    }

    private static int parseOrderNumber(Order order) {
        if (order == null || order.getOrderId() == null) {
            return Integer.MIN_VALUE;
        }
        String id = order.getOrderId().trim();
        if (!id.regionMatches(true, 0, "ORD-", 0, 4)) {
            return Integer.MIN_VALUE;
        }
        try {
            return Integer.parseInt(id.substring(4).trim());
        } catch (NumberFormatException ignored) {
            return Integer.MIN_VALUE;
        }
    }

    /** Active = not delivered and not cancelled. */
    private static boolean isActiveOrder(Order o) {

        if (o == null) {
            return false;
        }

        return !OrderRepository.isTerminalStatus(o.getStatus());
    }

    /** Upcoming = event date strictly after today. */
    private static boolean isUpcomingEvent(Order o) {

        if (o == null) {
            return false;
        }

        LocalDate d = o.getEventDate();

        return d != null && d.isAfter(LocalDate.now());
    }

    /** Open workflow = Pending or Preparing. */
    private static boolean isOpenWorkflow(Order o) {

        if (o == null) {
            return false;
        }

        String s = o.getStatus() == null
                ? ""
                : o.getStatus().trim();

        return s.equalsIgnoreCase("Pending")
                || s.equalsIgnoreCase("Preparing");
    }

    private static String safeText(String s) {
        if (s == null) {
            return "-";
        }
        String t = s.trim();
        return t.isEmpty() ? "-" : t;
    }

    private static String statusClass(String status) {
        String s = OrderRepository.normalizeStatus(status);
        return switch (s) {
            case "Preparing" -> "badge-preparing";
            case "Delivered" -> "badge-delivered";
            case "Cancelled" -> "badge-cancelled";
            default -> "badge-pending";
        };
    }

    private static final class StatusBadgeCell extends TableCell<Order, String> {
        private final Label badge = new Label();

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            }
            badge.setText(OrderRepository.normalizeStatus(item));
            badge.getStyleClass().setAll("status-mini-badge", statusClass(item));
            setGraphic(badge);
            setText(null);
        }
    }
}