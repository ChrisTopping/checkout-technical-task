package com.cpt.checkout.monetary;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

public class MonetaryFactory {
    public static MonetaryAmount pounds(double amountPounds) {
        return Monetary.getDefaultAmountFactory()
                .setCurrency("GBP")
                .setNumber(amountPounds)
                .create();
    }

    public static CurrencyUnit poundsCurrency() {
        return Monetary.getCurrency("GBP");
    }
}
