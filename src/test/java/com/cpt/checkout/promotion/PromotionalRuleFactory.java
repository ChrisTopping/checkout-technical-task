package com.cpt.checkout.promotion;

import com.cpt.checkout.monetary.MonetaryFactory;
import com.cpt.checkout.promotion.checkout.TotalPercentagePromotionalRule;
import com.cpt.checkout.promotion.item.FixedPricePromotionalRule;

public class PromotionalRuleFactory {

    public static TotalPercentagePromotionalRule totalPercentage(double thresholdTotalPounds, float percentDiscount) {
        return new TotalPercentagePromotionalRule(MonetaryFactory.pounds(thresholdTotalPounds), percentDiscount, MonetaryFactory.poundsCurrency(), 1);
    }

    public static TotalPercentagePromotionalRule totalPercentage(double thresholdTotalPounds, float percentDiscount, int priority) {
        return new TotalPercentagePromotionalRule(MonetaryFactory.pounds(thresholdTotalPounds), percentDiscount, MonetaryFactory.poundsCurrency(), priority);
    }

    public static FixedPricePromotionalRule fixedPrice(long itemId, long itemThreshold, double fixedPricePerItemPounds, int priority) {
        return new FixedPricePromotionalRule(itemId, itemThreshold, MonetaryFactory.pounds(fixedPricePerItemPounds), priority);
    }

}
