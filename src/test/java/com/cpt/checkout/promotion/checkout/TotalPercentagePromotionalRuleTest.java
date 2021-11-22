package com.cpt.checkout.promotion.checkout;

import com.cpt.checkout.checkout_item.CheckoutItem;
import com.cpt.checkout.checkout_item.CheckoutItemFactory;
import com.cpt.checkout.monetary.MonetaryFactory;
import com.cpt.checkout.promotion.PromotionalRuleFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TotalPercentagePromotionalRuleTest {

    @Nested
    @DisplayName("apply()")
    class Apply {

        @Test
        @DisplayName("Given checkoutLineItems is null: should throw NullPointerException")
        void givenCheckoutLineItemsIsNull_ShouldThrowNullPointerException() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0);

            // throw
            assertThrows(NullPointerException.class, () -> rule.apply(null));
        }

        @Test
        @DisplayName("Given checkoutLineItems is empty: should not modify list")
        void givenCheckoutLineItemsIsEmpty_ShouldNotModifyList() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(1, 0);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>();

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems).isEmpty();
        }

        @Test
        @DisplayName("Given checkout Line Items has a sum below threshold: should not modify list")
        void givenCheckoutLineItemsHasASumBelowThreshold_ShouldNotModifyList() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(2, 0);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>();
            checkoutLineItems.add(CheckoutItemFactory.productCheckoutItem(1, "product name", 1));

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems).hasSize(1);
        }

        @Test
        @DisplayName("Given checkoutLineItems has a sum equal to threshold: should add DiscountCheckoutItem to list")
        void givenCheckoutLineItemsHasASumEqualToThreshold_ShouldAddDiscountCheckoutItemToList() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(2, 50);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>();
            checkoutLineItems.add(CheckoutItemFactory.productCheckoutItem(1, "product name", 2));

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems)
                    .filteredOn(checkoutItem -> !checkoutItem.matchesItemId(1))
                    .hasSize(1)
                    .allMatch(checkoutItem -> checkoutItem.getPriceActual().equals(MonetaryFactory.pounds(-1)))
                    .allMatch(checkoutItem -> checkoutItem.getPromotionsApplied().size() == 1)
                    .allMatch(checkoutItem -> checkoutItem.getPromotionsApplied().contains(rule));
        }

        @Test
        @DisplayName("Given checkoutLineItems has a sum greater than threshold: should add DiscountCheckoutItem to list")
        void givenCheckoutLineItemsHasASumGreaterThanThreshold_ShouldAddDiscountCheckoutItemToList() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(2, 50);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>();
            checkoutLineItems.add(CheckoutItemFactory.productCheckoutItem(1, "product name", 3));

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems)
                    .filteredOn(checkoutItem -> !checkoutItem.matchesItemId(1))
                    .hasSize(1)
                    .allMatch(checkoutItem -> checkoutItem.getPriceActual().equals(MonetaryFactory.pounds(-1.5)))
                    .allMatch(checkoutItem -> checkoutItem.getPromotionsApplied().size() == 1)
                    .allMatch(checkoutItem -> checkoutItem.getPromotionsApplied().contains(rule));
        }
    }

    @Nested
    @DisplayName("compareTo()")
    class CompareTo {

        @Test
        @DisplayName("Given null: should throw NullPointerException")
        void givenNull_ShouldThrowNullPointerException() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0);

            // throw
            assertThrows(NullPointerException.class, () -> rule.compareTo(null));
        }

        @Test
        @DisplayName("Given rule with smaller priority: should return positive value")
        void givenRuleWithSmallerPriority_ShouldReturnPositiveValue() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0, 10);
            TotalPercentagePromotionalRule otherRule = PromotionalRuleFactory.totalPercentage(0, 0, 1);

            // when
            int result = rule.compareTo(otherRule);

            // assert
            assertThat(result).isPositive();
        }

        @Test
        @DisplayName("Given rule with larger priority: should return negative value")
        void givenRuleWithLargerPriority_ShouldReturnNegativeValue() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0, 1);
            TotalPercentagePromotionalRule otherRule = PromotionalRuleFactory.totalPercentage(0, 0, 10);

            // when
            int result = rule.compareTo(otherRule);

            // assert
            assertThat(result).isNegative();
        }

        @Test
        @DisplayName("Given rule with same priority: should return zero")
        void givenRuleWithSamePriority_ShouldReturnZero() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0, 1);
            TotalPercentagePromotionalRule otherRule = PromotionalRuleFactory.totalPercentage(0, 0, 1);

            // when
            int result = rule.compareTo(otherRule);

            // assert
            assertThat(result).isZero();
        }
    }
}