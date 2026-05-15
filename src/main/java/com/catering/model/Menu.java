package com.catering.model;

public class Menu {
    private final String mainDish;
    private final String drink;
    private final String dessert;

    public Menu(String mainDish, String drink, String dessert) {
        this.mainDish = mainDish;
        this.drink = drink;
        this.dessert = dessert;
    }

    public String getMainDish() {
        return mainDish;
    }

    public String getDrink() {
        return drink;
    }

    public String getDessert() {
        return dessert;
    }
}
