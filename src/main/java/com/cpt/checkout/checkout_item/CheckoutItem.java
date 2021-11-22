package com.cpt.checkout.checkout_item;

import com.cpt.checkout.promotion.PromotionalRule;

import javax.money.MonetaryAmount;
import java.util.List;

public interface CheckoutItem {
    MonetaryAmount getPriceActual();

    boolean matchesItemId(long itemId);

    void applyPromotion(MonetaryAmount amount, PromotionalRule promotionalRule);

    List<PromotionalRule> getPromotionsApplied();
}
