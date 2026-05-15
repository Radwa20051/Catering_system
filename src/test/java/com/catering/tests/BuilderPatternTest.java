package com.catering.tests;

import com.catering.patterns.builder.CateringMenuBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuilderPatternTest {
    @Test
    void buildsMenuStepByStep() {
        var menu = new CateringMenuBuilder()
                .addMainDish("Steak")
                .addDrink("Water")
                .addDessert("Cake")
                .buildMenu();
        assertEquals("Steak", menu.getMainDish());
    }
}
