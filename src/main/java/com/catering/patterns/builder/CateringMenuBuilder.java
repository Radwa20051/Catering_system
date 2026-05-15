package com.catering.patterns.builder;

import com.catering.model.Menu;

public class CateringMenuBuilder implements MenuBuilder {
    private String mainDish;
    private String drink;
    private String dessert;

    @Override
    public MenuBuilder addMainDish(String mainDish) {
        this.mainDish = mainDish;
        return this;
    }

    @Override
    public MenuBuilder addDrink(String drink) {
        this.drink = drink;
        return this;
    }

    @Override
    public MenuBuilder addDessert(String dessert) {
        this.dessert = dessert;
        return this;
    }

    @Override
    public Menu buildMenu() {
        if (mainDish == null || mainDish.isBlank() || drink == null || drink.isBlank() || dessert == null || dessert.isBlank()) {
            throw new IllegalArgumentException("Main dish, drink, and dessert are all required");
        }
        return new Menu(mainDish, drink, dessert);
    }
}
