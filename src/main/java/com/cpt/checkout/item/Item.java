package com.cpt.checkout.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.money.MonetaryAmount;

@AllArgsConstructor
public class Item {
    @Getter
    private final long id;
    @Getter
    private final String name;
    @Getter
    private final MonetaryAmount basePrice;
}
