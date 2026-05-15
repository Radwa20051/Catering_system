package com.catering.patterns.factory;

import com.catering.model.BirthdayEvent;
import com.catering.model.Event;
import com.catering.model.EventType;
import com.catering.model.MeetingEvent;
import com.catering.model.WeddingEvent;

public class EventFactory {
    public Event createEvent(EventType type) {
        if (type == null) {
            throw new IllegalArgumentException("Event type is required");
        }
        return switch (type) {
            case WEDDING -> new WeddingEvent();
            case MEETING -> new MeetingEvent();
            case BIRTHDAY -> new BirthdayEvent();
        };
    }

    /**
     * Maps free-text wizard labels (e.g. "Corporate Event", "Wedding") to a concrete {@link Event} via {@link EventType}.
     */
    public Event createEventFromWizardLabel(String wizardEventTypeLabel) {
        return createEvent(classifyWizardLabel(wizardEventTypeLabel));
    }

    private static EventType classifyWizardLabel(String label) {
        if (label == null || label.isBlank()) {
            return EventType.MEETING;
        }
        String s = label.toLowerCase();
        if (s.contains("wedding") || s.contains("engagement")) {
            return EventType.WEDDING;
        }
        if (s.contains("birthday")
                || s.contains("baby shower")
                || s.contains("kids party")
                || s.contains("gender reveal")) {
            return EventType.BIRTHDAY;
        }
        return EventType.MEETING;
    }
}
