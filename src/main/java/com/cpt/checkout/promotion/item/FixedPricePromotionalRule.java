package com.cpt.checkout.promotion.item;

import com.cpt.checkout.checkout_item.CheckoutItem;
import com.cpt.checkout.promotion.PromotionalRule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@RequiredArgsConstructor
public class FixedPricePromotionalRule implements PromotionalRule {

    private final long itemId;
    private final long itemThreshold;
    private final MonetaryAmount fixedPricePerItem;
    @Getter
    private final int priority;

    @Override
    public void apply(List<CheckoutItem> checkoutItems) {
        checkNotNull(checkoutItems, "checkoutItems must not be null");

        List<? extends CheckoutItem> matchingItems = checkoutItems.stream()
                .filter(checkoutItem -> checkoutItem.matchesItemId(itemId))
                .collect(Collectors.toList());

        if (matchingItems.size() >= itemThreshold) {
            matchingItems.forEach(checkoutItem -> checkoutItem.applyPromotion(fixedPricePerItem, this));
        }
    }

}
