package com.catering.ui.settings;

import com.catering.service.AppSettings;
import com.catering.ui.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleBox;

    @FXML private ComboBox<String> currencyBox;
    @FXML private ComboBox<String> defaultEventTypeBox;

    @FXML private CheckBox enableNotificationsBox;
    @FXML private CheckBox orderUpdatesBox;

    @FXML
    public void initialize() {
        if (roleBox != null && roleBox.getItems().isEmpty()) {
            roleBox.getItems().addAll("Admin", "Staff");
        }
        if (currencyBox != null && currencyBox.getItems().isEmpty()) {
            currencyBox.getItems().addAll("EGP", "USD");
        }
        if (defaultEventTypeBox != null && defaultEventTypeBox.getItems().isEmpty()) {
            defaultEventTypeBox.getItems().addAll("Wedding", "Birthday", "Corporate");
        }

        if (nameField != null && AppSettings.name != null) {
            nameField.setText(AppSettings.name);
        }
        if (emailField != null && AppSettings.email != null) {
            emailField.setText(AppSettings.email);
        }
        if (roleBox != null && AppSettings.role != null) {
            roleBox.setValue(AppSettings.role);
        }
        if (currencyBox != null && AppSettings.currency != null) {
            currencyBox.setValue(AppSettings.currency);
        }
        if (defaultEventTypeBox != null && AppSettings.defaultEventType != null) {
            defaultEventTypeBox.setValue(AppSettings.defaultEventType);
        }
        if (enableNotificationsBox != null) {
            enableNotificationsBox.setSelected(AppSettings.notificationsEnabled);
        }
    }

    @FXML
    public void onSaveSettings() {
        if (nameField != null) {
            AppSettings.name = nameField.getText();
        }
        if (emailField != null) {
            AppSettings.email = emailField.getText();
        }
        if (roleBox != null) {
            AppSettings.role = roleBox.getValue();
        }
        if (currencyBox != null) {
            AppSettings.currency = currencyBox.getValue();
        }
        if (defaultEventTypeBox != null) {
            AppSettings.defaultEventType = defaultEventTypeBox.getValue();
        }
        if (enableNotificationsBox != null) {
            AppSettings.notificationsEnabled = enableNotificationsBox.isSelected();
        }
        System.out.println("Settings saved");
    }

    @FXML
    private void onLogout() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/catering/ui/login/login-view.fxml")
            );

            Parent root = loader.load();

            /*
             * Get login controller
             */
            LoginController controller = loader.getController();

            /*
             * Current stage
             */
            Stage stage = (Stage) nameField.getScene().getWindow();

            /*
             * Reconnect stage
             */
            controller.setStage(stage);

            /*
             * IMPORTANT:
             * Reconnect login success navigation
             */
            controller.setOnLoginSuccess(v -> {

                try {

                    FXMLLoader appLoader = new FXMLLoader(
                            getClass().getResource("/com/catering/ui/main/main-shell.fxml")
                    );

                    Parent appRoot = appLoader.load();

                    Scene appScene = new Scene(appRoot);

                    appScene.getStylesheets().add(
                            getClass()
                                    .getResource("/com/catering/styles/app.css")
                                    .toExternalForm()
                    );

                    stage.setScene(appScene);

                    stage.show();

                } catch (IOException ex) {

                    ex.printStackTrace();
                }
            });

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass()
                            .getResource("/com/catering/styles/app.css")
                            .toExternalForm()
            );

            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
