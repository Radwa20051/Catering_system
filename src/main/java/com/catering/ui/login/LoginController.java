package com.catering.ui.login;

import com.catering.service.ApplicationContext;
import com.catering.service.AuthService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.util.function.Consumer;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label validationHint;
    @FXML private Button loginButton;
    @FXML private StackPane loadingOverlay;
    @FXML private ProgressIndicator loginProgress;

    private final AuthService authService = new AuthService();
    private Consumer<Void> onSuccess;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnLoginSuccess(Consumer<Void> onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    public void initialize() {
        validationHint.setText("");
    }

    @FXML
    protected void onLogin() {
        clearFieldErrors();
        validationHint.setText("");

        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        boolean emailOk = isValidEmail(email);
        boolean passwordOk = password.length() >= 6;

        if (!emailOk) {
            emailField.getStyleClass().add("error");
            validationHint.setText("Enter a valid email address.");
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Invalid Email Format");
            alert.setHeaderText("Please check your email address.");
            alert.setContentText("The email format entered is incorrect (e.g., user@domain.com).");
            if (stage != null) alert.initOwner(stage);
            alert.showAndWait();
            return;
        }
        if (!passwordOk) {
            passwordField.getStyleClass().add("error");
            validationHint.setText("Password must be at least 6 characters.");
            return;
        }

        loginButton.setDisable(true);
        loadingOverlay.setVisible(true);
        loadingOverlay.setManaged(true);
        loginProgress.setProgress(-1);

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws InterruptedException {
                Thread.sleep(450);
                return authService.login(email, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            boolean ok = loginTask.getValue();
            loginButton.setDisable(false);
            loadingOverlay.setVisible(false);
            loadingOverlay.setManaged(false);
            if (ok) {
                ApplicationContext.session().setAuthenticated(true);
                ApplicationContext.session().setUserEmail(email);
                var n = Notifications.create()
                        .title("Welcome")
                        .text("Signed in successfully.");
                if (stage != null) {
                    n.owner(stage);
                }
                n.showInformation();
                if (onSuccess != null) {
                    onSuccess.accept(null);
                }
            } else {
                validationHint.setText("Invalid email or password.");
                passwordField.getStyleClass().add("error");
                var nf = Notifications.create()
                        .title("Login failed")
                        .text("Check your credentials and try again.");
                if (stage != null) {
                    nf.owner(stage);
                }
                nf.showError();
            }
        });

        loginTask.setOnFailed(e -> {
            loginButton.setDisable(false);
            loadingOverlay.setVisible(false);
            loadingOverlay.setManaged(false);
            Throwable ex = loginTask.getException();
            validationHint.setText(ex != null ? ex.getMessage() : "Something went wrong.");
            Platform.runLater(() -> {
                var ne = Notifications.create()
                        .title("Error")
                        .text(validationHint.getText());
                if (stage != null) {
                    ne.owner(stage);
                }
                ne.showError();
            });
        });

        new Thread(loginTask, "catering-login").start();
    }

    private void clearFieldErrors() {
        emailField.getStyleClass().remove("error");
        passwordField.getStyleClass().remove("error");
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');
        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }
}
