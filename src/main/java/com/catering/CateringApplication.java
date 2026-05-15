package com.catering;

import com.catering.ui.login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CateringApplication extends Application {

    private static final String APP_CSS = "/com/catering/styles/app.css";

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Smart Event Catering System");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        showLogin(stage);
        stage.show();
    }

    private void showLogin(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(CateringApplication.class.getResource("/com/catering/ui/login/login-view.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setStage(stage);
        loginController.setOnLoginSuccess(v -> showMain(stage));
        Scene scene = new Scene(root, 960, 640);
        applyGlobalStylesheets(scene);
        stage.setScene(scene);
    }

    private void showMain(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(CateringApplication.class.getResource("/com/catering/ui/main/main-shell.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 800);
            applyGlobalStylesheets(scene);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load main shell", e);
        }
    }

    private void applyGlobalStylesheets(Scene scene) {
        URL appCss = Objects.requireNonNull(CateringApplication.class.getResource(APP_CSS), APP_CSS);
        scene.getStylesheets().add(appCss.toExternalForm());
        URL bootstrap = CateringApplication.class.getResource("/org/kordamp/bootstrapfx/bootstrapfx.css");
        if (bootstrap != null) {
            scene.getStylesheets().add(bootstrap.toExternalForm());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
