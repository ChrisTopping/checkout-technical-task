package com.cpt.checkout.checkout_item;

import com.cpt.checkout.item.Item;
import com.cpt.checkout.promotion.PromotionalRule;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Builder(access = AccessLevel.PRIVATE)
public class ProductCheckoutItem implements CheckoutItem {
    @Getter
    private Item item;
    @Getter
    private MonetaryAmount priceActual;
    @Getter
    private List<PromotionalRule> promotionsApplied;

    public static ProductCheckoutItem fromItem(Item item) {
        checkNotNull(item, "item must not be null");

        return ProductCheckoutItem.builder()
                .item(item)
                .priceActual(item.getBasePrice())
                .promotionsApplied(new ArrayList<>())
                .build();
    }

    @Override
    public boolean matchesItemId(long id) {
        return id == item.getId();
    }

    @Override
    public void applyPromotion(MonetaryAmount amount, PromotionalRule promotionalRule) {
        checkNotNull(amount, "amount must not be null");
        checkNotNull(promotionalRule, "promotionalRule must not be null");

        priceActual = amount;
        promotionsApplied.add(promotionalRule);
    }
}
