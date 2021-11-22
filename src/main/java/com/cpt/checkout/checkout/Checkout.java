package com.cpt.checkout.checkout;

import com.cpt.checkout.checkout_item.CheckoutItem;
import com.cpt.checkout.item.Item;

import javax.money.MonetaryAmount;
import java.util.List;

public interface Checkout {
    void scanItems(List<Item> items);

    List<CheckoutItem> getCheckoutItems();

    MonetaryAmount total();
}
