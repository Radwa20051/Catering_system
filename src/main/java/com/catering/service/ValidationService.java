package com.catering.service;

import com.catering.model.Event;
import com.catering.model.Menu;

import java.time.LocalDate;

public class ValidationService {
    public void validateEventDetails(int guestCount, String location, LocalDate date) {
        if (guestCount <= 0) {
            throw new IllegalArgumentException("Guest count must be greater than zero");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location is required");
        }
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date must be today or later");
        }
    }

    public void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event is required");
        }
        validateEventDetails(event.getGuestCount(), event.getLocation(), event.getDate());
    }

    public void validateMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu is required");
        }
    }
}
