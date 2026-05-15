package com.catering.ui.main;

import com.catering.ui.calendar.CalendarController;
import com.catering.ui.customer.CustomerRepository;
import com.catering.ui.dashboard.DashboardController;
import com.catering.ui.notifications.AppNotificationRepository;
import com.catering.ui.notifications.NotificationsController;
import com.catering.ui.orders.OrdersController;
import com.catering.ui.customers.CustomersController;
import com.catering.ui.order.OrderRepository;
import com.catering.ui.tracking.TrackingController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainShellController {

    @FXML private BorderPane appRoot;
    @FXML private Label pageTitle;
    @FXML private Label pageSubtitle;
    @FXML private StackPane contentHost;
    @FXML private Button navDashboard;
    @FXML private Button navNewOrder;
    @FXML private Button navOrders;
    @FXML private Button navCalendar;
    @FXML private Button navCustomers;
    @FXML private Button navTracking;
    @FXML private Button navNotifications;
    @FXML private Button navSettings;

    private final Map<Nav, Button> navButtons = new HashMap<>();
    private DashboardController dashboardControllerRef;
    private OrdersController ordersControllerRef;
    private CustomersController customersControllerRef;
    private TrackingController trackingControllerRef;
    private NotificationsController notificationsControllerRef;
    private CalendarController calendarControllerRef;

    private enum Nav {
        DASHBOARD, NEW_ORDER, ORDERS, CALENDAR, CUSTOMERS, TRACKING, NOTIFICATIONS, SETTINGS
    }

    @FXML
    public void initialize() {
        navButtons.put(Nav.DASHBOARD, navDashboard);
        navButtons.put(Nav.NEW_ORDER, navNewOrder);
        navButtons.put(Nav.ORDERS, navOrders);
        navButtons.put(Nav.CALENDAR, navCalendar);
        navButtons.put(Nav.CUSTOMERS, navCustomers);
        navButtons.put(Nav.TRACKING, navTracking);
        navButtons.put(Nav.NOTIFICATIONS, navNotifications);
        navButtons.put(Nav.SETTINGS, navSettings);

        styleNavIcon(navDashboard, FontAwesomeSolid.HOME);
        styleNavIcon(navNewOrder, FontAwesomeSolid.PLUS);
        styleNavIcon(navOrders, FontAwesomeSolid.LIST);
        styleNavIcon(navCalendar, FontAwesomeSolid.CALENDAR);
        styleNavIcon(navCustomers, FontAwesomeSolid.USERS);
        styleNavIcon(navTracking, FontAwesomeSolid.TRUCK);
        styleNavIcon(navNotifications, FontAwesomeSolid.BELL);
        styleNavIcon(navSettings, FontAwesomeSolid.COG);

        registerDataRefreshHooks();
        openDashboard();
    }

    private void registerDataRefreshHooks() {
        Runnable refreshAll = () -> {
            if (dashboardControllerRef != null) {
                dashboardControllerRef.refresh();
            }
            if (ordersControllerRef != null) {
                ordersControllerRef.refresh();
            }
            if (customersControllerRef != null) {
                customersControllerRef.refresh();
            }
            if (trackingControllerRef != null) {
                trackingControllerRef.refresh();
            }
            if (notificationsControllerRef != null) {
                notificationsControllerRef.refresh();
            }
            if (calendarControllerRef != null) {
                calendarControllerRef.refresh();
            }
        };
        OrderRepository.addListener(refreshAll);
        CustomerRepository.addListener(refreshAll);
        AppNotificationRepository.addListener(refreshAll);
    }

    private void handleLoadError(String page, IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load error");
        alert.setHeaderText("Could not load " + page);
        alert.setContentText(buildCauseMessage(e));

        TextArea details = new TextArea(stackTrace(e));
        details.setEditable(false);
        details.setWrapText(false);
        details.setPrefRowCount(14);
        details.setPrefColumnCount(80);
        alert.getDialogPane().setExpandableContent(details);
        alert.showAndWait();
    }

    private String buildCauseMessage(Throwable t) {
        if (t == null) return "Unknown error";
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String top = t.getMessage() != null ? t.getMessage() : t.toString();
        String bottom = root.getMessage() != null ? root.getMessage() : root.toString();
        if (top.equals(bottom)) return top;
        return top + "\n\nCause: " + bottom;
    }

    private String stackTrace(Throwable t) {
        if (t == null) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private void styleNavIcon(Button btn, FontAwesomeSolid glyph) {
        FontIcon icon = new FontIcon(glyph);
        icon.setIconSize(16);
        icon.setIconColor(Color.rgb(255, 255, 255, 0.92));
        btn.setGraphic(icon);
    }

    private void setActiveNav(Nav active) {
        navButtons.values().forEach(b -> b.getStyleClass().remove("side-nav-btn-active"));
        Button selected = navButtons.get(active);
        if (selected != null) {
            selected.getStyleClass().add("side-nav-btn-active");
        }
    }

    private Parent load(String resource) throws IOException {
        URL url = getClass().getResource(resource);

        System.out.println("Loading: " + resource);
        System.out.println("Resolved URL: " + url);

        if (url == null) {
            throw new RuntimeException("❌ FXML not found: " + resource);
        }

        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }

    private void showContent(Parent node, Nav nav, String title, String subtitle) {
        contentHost.getChildren().setAll(node);
        pageTitle.setVisible(!title.isBlank());
        pageTitle.setManaged(!title.isBlank());
        pageTitle.setText(title);

        pageSubtitle.setVisible(!subtitle.isBlank());
        pageSubtitle.setManaged(!subtitle.isBlank());
        pageSubtitle.setText(subtitle);
        setActiveNav(nav);
    }

    @FXML
    protected void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource("/com/catering/ui/dashboard/dashboard-view.fxml"));
            Parent root = loader.load();
            dashboardControllerRef = loader.getController();
            if (dashboardControllerRef != null) {
                dashboardControllerRef.setNavigationActions(
                        this::openOrders,
                        this::openCalendar,
                        this::openNewOrder,
                        this::openCustomers,
                        this::openTracking
                );
                dashboardControllerRef.refresh();
            }
            showContent(root, Nav.DASHBOARD, "", "");
        } catch (IOException e) {
            handleLoadError("Dashboard", e);
        }
    }

    @FXML
    protected void openNewOrder() {
        try {
            Parent root = load("/com/catering/ui/order/new-order-wizard.fxml");
            showContent(root, Nav.NEW_ORDER,
                    "Create Catering Order",
                    "Complete event and menu details");
        } catch (IOException e) {
            handleLoadError("New Order", e);
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load error");
            alert.setHeaderText("Could not load New Order");
            alert.setContentText(buildCauseMessage(e));

            TextArea details = new TextArea(stackTrace(e));
            details.setEditable(false);
            details.setWrapText(false);
            details.setPrefRowCount(14);
            details.setPrefColumnCount(80);
            alert.getDialogPane().setExpandableContent(details);
            alert.showAndWait();
        }
    }

    @FXML
    protected void openOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource("/com/catering/ui/orders/orders-view.fxml"));
            Parent root = loader.load();
            ordersControllerRef = loader.getController();
            if (ordersControllerRef != null) {
                ordersControllerRef.refresh();
            }
            showContent(root, Nav.ORDERS,
                    "Orders Management",
                    "Track and manage catering requests");
        } catch (IOException e) {
            handleLoadError("Orders", e);
        }
    }

    @FXML
    protected void openCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource("/com/catering/ui/calendar/calendar-view.fxml"));
            Parent root = loader.load();
            calendarControllerRef = loader.getController();
            if (calendarControllerRef != null) {
                calendarControllerRef.refresh();
            }
            showContent(root, Nav.CALENDAR,
                    "Event Calendar",
                    "Upcoming catering schedule");
        } catch (IOException e) {
            handleLoadError("Calendar", e);
        }
    }

    @FXML
    protected void openCustomers() {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource("/com/catering/ui/customers/customers-view.fxml"));
            Parent root = loader.load();
            customersControllerRef = loader.getController();
            if (customersControllerRef != null) {
                customersControllerRef.refresh();
            }
            showContent(root, Nav.CUSTOMERS,
                    "Customer Management",
                    "View customer profiles and activity");
        } catch (IOException e) {
            handleLoadError("Customers", e);
        }
    }

    @FXML
    protected void openTracking() {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource("/com/catering/ui/tracking/tracking-view.fxml"));
            Parent root = loader.load();
            trackingControllerRef = loader.getController();
            if (trackingControllerRef != null) {
                trackingControllerRef.refresh();
            }
            showContent(root, Nav.TRACKING,
                    "Order Tracking",
                    "Monitor live catering workflow");
        } catch (IOException e) {
            handleLoadError("Tracking", e);
        }
    }

    @FXML
    protected void openNotifications() {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource("/com/catering/ui/notifications/notifications-view.fxml"));
            Parent root = loader.load();
            notificationsControllerRef = loader.getController();
            if (notificationsControllerRef != null) {
                notificationsControllerRef.refresh();
            }
            showContent(root, Nav.NOTIFICATIONS,
                    "Recent Activity",
                    "System alerts and catering updates appear here.");
        } catch (IOException e) {
            handleLoadError("Notifications", e);
        }
    }

    @FXML
    protected void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource("/com/catering/ui/settings/settings-view.fxml"));
            Parent root = loader.load();
            showContent(root, Nav.SETTINGS,
                    "System Settings",
                    "Customize your workspace");
        } catch (IOException e) {
            handleLoadError("Settings", e);
        }
    }
}
