package com.catering.ui.components;

import javafx.stage.Window;
import org.controlsfx.control.Notifications;

/**
 * Small wrapper around ControlsFX notifications for consistent toasts.
 */
public final class ToastHelper {

    private ToastHelper() {
    }

    public static void info(Window owner, String title, String message) {
        var n = Notifications.create().title(title).text(message);
        if (owner != null) {
            n.owner(owner);
        }
        n.showInformation();
    }

    public static void error(Window owner, String title, String message) {
        var n = Notifications.create().title(title).text(message);
        if (owner != null) {
            n.owner(owner);
        }
        n.showError();
    }
}
