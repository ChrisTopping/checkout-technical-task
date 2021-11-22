package com.cpt.checkout.promotion.checkout;

import com.cpt.checkout.checkout_item.CheckoutItem;
import com.cpt.checkout.checkout_item.DiscountCheckoutItem;
import com.cpt.checkout.promotion.PromotionalRule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@RequiredArgsConstructor
public class TotalPercentagePromotionalRule implements PromotionalRule {

    private final MonetaryAmount thresholdTotal;
    private final float percentDiscount;
    private final CurrencyUnit currencyUnit;
    @Getter
    private final int priority;

    @Override
    public void apply(List<CheckoutItem> checkoutItems) {
        checkNotNull(checkoutItems, "checkoutItems must not be null");

        MonetaryAmount checkoutTotal = checkoutItems.stream()
                .map(CheckoutItem::getPriceActual)
                .reduce(MonetaryFunctions::sum)
                .orElse(Money.of(0, currencyUnit));

        if (checkoutTotal.isGreaterThanOrEqualTo(thresholdTotal)) {
            // negative amount represents reduction in checkout total
            MonetaryAmount discountAmount = checkoutTotal.multiply(-percentDiscount / 100);
            DiscountCheckoutItem discountCheckoutItem = DiscountCheckoutItem.fromPriceAndPromotion(discountAmount, this);
            checkoutItems.add(discountCheckoutItem);
        }
    }
}
