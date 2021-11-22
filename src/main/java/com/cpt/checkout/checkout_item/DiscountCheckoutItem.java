package com.cpt.checkout.checkout_item;

import com.cpt.checkout.promotion.PromotionalRule;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Builder(access = AccessLevel.PRIVATE)
public class DiscountCheckoutItem implements CheckoutItem {

    @Getter
    private MonetaryAmount priceActual;
    @Getter
    private List<PromotionalRule> promotionsApplied;

    public static DiscountCheckoutItem fromPriceAndPromotion(MonetaryAmount priceActual, PromotionalRule promotionApplied) {
        checkNotNull(priceActual, "priceActual must not be null");
        checkNotNull(promotionApplied, "promotionApplied must not be null");

        ArrayList<PromotionalRule> promotionsApplied = new ArrayList<>();
        promotionsApplied.add(promotionApplied);
        return DiscountCheckoutItem.builder()
                .priceActual(priceActual)
                .promotionsApplied(promotionsApplied)
                .build();
    }

    @Override
    public boolean matchesItemId(long itemId) {
        return false;
    }

    @Override
    public void applyPromotion(MonetaryAmount amount, PromotionalRule promotionalRule) {
        checkNotNull(amount, "amount must not be null");
        checkNotNull(promotionalRule, "promotionalRule must not be null");

        priceActual = amount;
        promotionsApplied.add(promotionalRule);
    }
}
