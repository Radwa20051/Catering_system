package com.catering.tests;

import com.catering.model.EventType;
import com.catering.patterns.factory.EventFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryPatternTest {
    @Test
    void createsExpectedEventType() {
        EventFactory factory = new EventFactory();
        assertEquals(EventType.WEDDING, factory.createEvent(EventType.WEDDING).getType());
    }

    @Test
    void mapsWizardStringsToConcreteEvents() {
        EventFactory factory = new EventFactory();
        assertEquals(EventType.WEDDING, factory.createEventFromWizardLabel("Outdoor Wedding").getType());
        assertEquals(EventType.BIRTHDAY, factory.createEventFromWizardLabel("Kids Party").getType());
        assertEquals(EventType.MEETING, factory.createEventFromWizardLabel("Corporate Event").getType());
    }
}
