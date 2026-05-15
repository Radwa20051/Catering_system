package com.catering.tests;

import com.catering.patterns.decorator.BaseOrderComponent;
import com.catering.patterns.decorator.DessertAddonDecorator;
import com.catering.patterns.decorator.DrinkAddonDecorator;
import com.catering.patterns.decorator.FlatFeeAddonDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecoratorPatternTest {
    @Test
    void appliesMultipleAddons() {
        var order = new DrinkAddonDecorator(new DessertAddonDecorator(new BaseOrderComponent(1000)));
        assertTrue(order.getCost() > 1000);
    }

    @Test
    void flatFeeDecoratorAddsConfiguredAmount() {
        var order = new FlatFeeAddonDecorator(new BaseOrderComponent(100), 12.5, "catering add-on");
        assertEquals(112.5, order.getCost(), 0.001);
    }
}
