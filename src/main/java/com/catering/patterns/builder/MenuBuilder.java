package com.catering.patterns.builder;

import com.catering.model.Menu;

public interface MenuBuilder {
    MenuBuilder addMainDish(String mainDish);
    MenuBuilder addDrink(String drink);
    MenuBuilder addDessert(String dessert);
    Menu buildMenu();
}
