package com.cpt.checkout.promotion;

import com.cpt.checkout.checkout_item.CheckoutItem;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public interface PromotionalRule extends Comparable<PromotionalRule> {

    void apply(List<CheckoutItem> checkoutLineItems);

    int getPriority();

    default int compareTo(PromotionalRule o) {
        checkNotNull(o);
        return getPriority() - o.getPriority();
    }

}
