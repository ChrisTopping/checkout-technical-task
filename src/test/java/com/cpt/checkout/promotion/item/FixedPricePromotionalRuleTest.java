package com.cpt.checkout.promotion.item;

import com.cpt.checkout.checkout_item.CheckoutItem;
import com.cpt.checkout.checkout_item.CheckoutItemFactory;
import com.cpt.checkout.monetary.MonetaryFactory;
import com.cpt.checkout.promotion.PromotionalRuleFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedPricePromotionalRuleTest {

    @Nested
    @DisplayName("apply()")
    class Apply {

        @Test
        @DisplayName("Given checkoutLineItems is null: should throw NullPointerException")
        void givenCheckoutLineItemsIsNull_ShouldThrowNullPointerException() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(2, 2, 2.0, 1);

            // throw
            assertThrows(NullPointerException.class, () -> rule.apply(null));
        }

        @Test
        @DisplayName("Given checkoutLineItems is empty: should not modify list")
        void givenCheckoutLineItemsIsEmpty_ShouldNotModifyList() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(2, 2, 2.0, 1);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>();

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems).isEmpty();
        }

        @Test
        @DisplayName("Given checkoutLineItems contains no rule items: should not modify items")
        void givenCheckoutLineItemsContainsNoRuleItems_ShouldNotModifyList() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(2, 2, 2.0, 1);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>();
            checkoutLineItems.add(CheckoutItemFactory.productCheckoutItem(1, "product name", 1));

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems)
                    .hasSize(1)
                    .extracting(CheckoutItem::getPriceActual)
                    .containsOnly(MonetaryFactory.pounds(1));
        }

        @Test
        @DisplayName("Given checkoutLineItems contains fewer rule items than threshold: should not modify items")
        void givenCheckoutLineItemsContainsFewerRuleItemsThanThreshold_ShouldNotModifyList() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(2, 2, 2.0, 1);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>(asList(
                    CheckoutItemFactory.productCheckoutItem(1, "product name", 1),
                    CheckoutItemFactory.productCheckoutItem(2, "product name", 3)
            ));

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems)
                    .hasSize(2)
                    .extracting(CheckoutItem::getPriceActual)
                    .containsOnly(MonetaryFactory.pounds(1), MonetaryFactory.pounds(3));
        }

        @Test
        @DisplayName("Given checkoutLineItems contains the threshold number of rule items: should modify price of all rule items")
        void givenCheckoutLineItemsHasASumEqualToThreshold_ShouldAddDiscountCheckoutItemToList() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(2, 2, 2.0, 1);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>(asList(
                    CheckoutItemFactory.productCheckoutItem(1, "product name", 1),
                    CheckoutItemFactory.productCheckoutItem(2, "product name", 3),
                    CheckoutItemFactory.productCheckoutItem(2, "product name", 3)
            ));

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems)
                    .hasSize(3)
                    .extracting(CheckoutItem::getPriceActual)
                    .containsOnly(
                            MonetaryFactory.pounds(1),
                            MonetaryFactory.pounds(2),
                            MonetaryFactory.pounds(2)
                    );
        }

        @Test
        @DisplayName("Given checkoutLineItems contains greater rule items than threshold: should modify price of all rule items")
        void givenCheckoutLineItemsHasASumGreaterThanThreshold_ShouldAddDiscountCheckoutItemToList() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(2, 2, 2.0, 1);
            ArrayList<CheckoutItem> checkoutLineItems = new ArrayList<>(asList(
                    CheckoutItemFactory.productCheckoutItem(1, "product name", 1),
                    CheckoutItemFactory.productCheckoutItem(2, "product name", 3),
                    CheckoutItemFactory.productCheckoutItem(2, "product name", 3),
                    CheckoutItemFactory.productCheckoutItem(2, "product name", 3)
            ));

            // when
            rule.apply(checkoutLineItems);

            // assert
            assertThat(checkoutLineItems)
                    .hasSize(4)
                    .extracting(CheckoutItem::getPriceActual)
                    .containsOnly(
                            MonetaryFactory.pounds(1),
                            MonetaryFactory.pounds(2),
                            MonetaryFactory.pounds(2),
                            MonetaryFactory.pounds(2)
                    );
        }
    }

    @Nested
    @DisplayName("compareTo()")
    class CompareTo {

        @Test
        @DisplayName("Given null: should throw NullPointerException")
        void givenNull_ShouldThrowNullPointerException() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(0, 0, 0.0, 1);

            // throw
            assertThrows(NullPointerException.class, () -> rule.compareTo(null));
        }

        @Test
        @DisplayName("Given rule with smaller priority: should return positive value")
        void givenRuleWithSmallerPriority_ShouldReturnPositiveValue() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(0, 0, 0.0, 10);
            FixedPricePromotionalRule otherRule = PromotionalRuleFactory.fixedPrice(0, 0, 0, 1);

            // when
            int result = rule.compareTo(otherRule);

            // assert
            assertThat(result).isPositive();
        }

        @Test
        @DisplayName("Given rule with larger priority: should return negative value")
        void givenRuleWithLargerPriority_ShouldReturnNegativeValue() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(0, 0, 0.0, 1);
            FixedPricePromotionalRule otherRule = PromotionalRuleFactory.fixedPrice(0, 0, 0, 10);

            // when
            int result = rule.compareTo(otherRule);

            // assert
            assertThat(result).isNegative();
        }

        @Test
        @DisplayName("Given rule with same priority: should return zero")
        void givenRuleWithSamePriority_ShouldReturnZero() {
            // given
            FixedPricePromotionalRule rule = PromotionalRuleFactory.fixedPrice(0, 0, 0.0, 1);
            FixedPricePromotionalRule otherRule = PromotionalRuleFactory.fixedPrice(0, 0, 0, 1);

            // when
            int result = rule.compareTo(otherRule);

            // assert
            assertThat(result).isZero();
        }
    }

}