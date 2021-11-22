package com.cpt.checkout.checkout;

import com.cpt.checkout.checkout_item.CheckoutItem;
import com.cpt.checkout.checkout_item.ProductCheckoutItem;
import com.cpt.checkout.item.Item;
import com.cpt.checkout.promotion.PromotionalRule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@RequiredArgsConstructor
public class CheckoutWithPromotions implements Checkout {

    private final Set<PromotionalRule> promotionalRules;
    private final CurrencyUnit currencyUnit;

    @Getter
    private List<CheckoutItem> checkoutItems = new ArrayList<>();

    @Override
    public void scanItems(List<Item> items) {
        checkNotNull(items, "items list must not be null");

        if (items.isEmpty()) {
            return;
        }

        List<CheckoutItem> checkoutItems = items.stream()
                .map(ProductCheckoutItem::fromItem)
                .collect(Collectors.toList());

        promotionalRules.stream()
                .sorted()
                .forEach(promotionalRule -> promotionalRule.apply(checkoutItems));

        this.checkoutItems = checkoutItems;
    }

    @Override
    public MonetaryAmount total() {
        return checkoutItems.stream()
                .map(CheckoutItem::getPriceActual)
                .reduce(MonetaryFunctions::sum)
                .orElse(Money.of(BigDecimal.ZERO, currencyUnit))
                .with(Monetary.getDefaultRounding());
    }
}
