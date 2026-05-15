package com.catering.ui.notifications;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory notification log for UI (order lifecycle). Listeners mirror {@link com.catering.ui.order.OrderRepository}.
 */
public final class AppNotificationRepository {

    private static final List<AppNotification> ENTRIES = new ArrayList<>();
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();
    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private AppNotificationRepository() {}

    public static void addListener(Runnable r) {
        if (r == null) {
            return;
        }
        LISTENERS.add(r);
    }

    public static void removeListener(Runnable r) {
        LISTENERS.remove(r);
    }

    private static void notifyListeners() {
        for (Runnable r : LISTENERS) {
            try {
                r.run();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void addEntry(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        synchronized (ENTRIES) {
            ENTRIES.add(0, new AppNotification(Instant.now(), message.trim()));
        }
        notifyListeners();
    }

    public static List<AppNotification> getEntriesNewestFirst() {
        synchronized (ENTRIES) {
            return Collections.unmodifiableList(new ArrayList<>(ENTRIES));
        }
    }

    public static void clear() {
        synchronized (ENTRIES) {
            ENTRIES.clear();
        }
        notifyListeners();
    }

    public static final class AppNotification {
        private final Instant timestamp;
        private final String message;

        public AppNotification(Instant timestamp, String message) {
            this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
            this.message = Objects.requireNonNull(message, "message");
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public String getMessage() {
            return message;
        }

        public String formatLine() {
            return TS.format(timestamp) + " — " + message;
        }
    }
}
