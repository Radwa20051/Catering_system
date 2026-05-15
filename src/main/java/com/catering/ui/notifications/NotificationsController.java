package com.catering.ui.notifications;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class NotificationsController {

    @FXML
    private ListView<String> notificationsList;

    @FXML
    private VBox emptyStateBox;

    @FXML
    public void initialize() {
        refresh();
    }

    public void refresh() {

        if (notificationsList == null) {
            return;
        }

        var lines = AppNotificationRepository
                .getEntriesNewestFirst()
                .stream()
                .map(AppNotificationRepository.AppNotification::formatLine)
                .collect(Collectors.toList());

        notificationsList.setItems(
                FXCollections.observableArrayList(lines)
        );

        /*
         * Empty state
         */
        boolean empty = lines.isEmpty();

        if (emptyStateBox != null) {

            emptyStateBox.setVisible(empty);
            emptyStateBox.setManaged(empty);
        }
    }
}

