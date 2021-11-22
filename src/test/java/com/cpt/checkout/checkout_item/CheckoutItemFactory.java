package com.cpt.checkout.checkout_item;

import com.cpt.checkout.item.ItemFactory;

public class CheckoutItemFactory {

    public static CheckoutItem productCheckoutItem(long id, String name, double amountPounds) {
        return ProductCheckoutItem.fromItem(ItemFactory.item(id, name, amountPounds));
    }

}
