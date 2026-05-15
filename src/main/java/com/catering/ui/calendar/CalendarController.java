package com.catering.ui.calendar;

import com.catering.ui.customer.Customer;
import com.catering.ui.order.Order;
import com.catering.ui.order.OrderRepository;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarController {

    private static final DateTimeFormatter HEADER = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy");

    @FXML private VBox eventsByDateBox;

    @FXML
    public void initialize() {
        refresh();
    }

    public void refresh() {
        if (eventsByDateBox == null) {
            return;
        }
        eventsByDateBox.getChildren().clear();

        List<Order> orders = new ArrayList<>(OrderRepository.getAllOrders());
        Map<String, List<Order>> grouped = new LinkedHashMap<>();
        for (Order o : orders) {
            String key = groupKey(o);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(o);
        }

        List<String> keys = new ArrayList<>(grouped.keySet());
        keys.sort(dateKeyComparator());

        for (String key : keys) {
            Label header = new Label(formatHeader(key));
            header.getStyleClass().add("wizard-title");
            eventsByDateBox.getChildren().add(header);
            for (Order o : grouped.get(key)) {
                VBox card = new VBox(6);
                card.getStyleClass().add("card-surface");
                card.setPadding(new Insets(10, 14, 10, 14));
                card.getChildren().add(new Label(summaryLine1(o)));
                card.getChildren().add(new Label(summaryLine2(o)));
                eventsByDateBox.getChildren().add(card);
            }
        }

        if (eventsByDateBox.getChildren().isEmpty()) {
            Label empty = new Label("No scheduled events yet.");
            empty.getStyleClass().add("content-header-sub");
            eventsByDateBox.getChildren().add(empty);
        }
    }

    private static Comparator<String> dateKeyComparator() {
        return (a, b) -> {
            if ("Unscheduled".equals(a) && !"Unscheduled".equals(b)) {
                return 1;
            }
            if (!"Unscheduled".equals(a) && "Unscheduled".equals(b)) {
                return -1;
            }
            return a.compareTo(b);
        };
    }

    private static String groupKey(Order o) {
        if (o == null || o.getEventDate() == null) {
            return "Unscheduled";
        }
        return o.getEventDate().toString();
    }

    private static String formatHeader(String key) {
        if ("Unscheduled".equals(key)) {
            return "Unscheduled";
        }
        try {
            return LocalDate.parse(key).format(HEADER);
        } catch (Exception ex) {
            return key;
        }
    }

    private static String summaryLine1(Order o) {
        String id = o.getOrderId() != null ? o.getOrderId() : "—";
        return id + " · " + customerLabel(o.getCustomer());
    }

    private static String summaryLine2(Order o) {
        String type = safeText(o.getEventType());
        String guests = o.getGuestCount() > 0 ? o.getGuestCount() + " guests" : "—";
        String loc = safeText(o.getLocation());
        return type + " · " + guests + " · " + loc;
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
}
