package com.cpt.checkout.checkout_item;

import com.cpt.checkout.monetary.MonetaryFactory;
import com.cpt.checkout.promotion.PromotionalRuleFactory;
import com.cpt.checkout.promotion.checkout.TotalPercentagePromotionalRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscountCheckoutItemTest {

    @Nested
    @DisplayName("fromPriceAndPromotion()")
    class FromPriceAndPromotion {

        @Test
        @DisplayName("Given null priceActual: should throw NullPointerException")
        void givenNullPriceActual_shouldThrowNullPointerException() {
            // throw
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0);
            assertThrows(NullPointerException.class, () -> DiscountCheckoutItem.fromPriceAndPromotion(null, rule));
        }

        @Test
        @DisplayName("Given null promotionalRule: should throw NullPointerException")
        void givenNullPromotionalRule_shouldThrowNullPointerException() {
            // throw
            MonetaryAmount monetaryAmount = MonetaryFactory.pounds(0);
            assertThrows(NullPointerException.class, () -> DiscountCheckoutItem.fromPriceAndPromotion(monetaryAmount, null));
        }

        @Test
        @DisplayName("Given non-null priceActual and promotionalRule: should return DiscountCheckoutItem with priceActual")
        void givenNonNullPriceActualAndPromotionalRule_ShouldReturnDiscountCheckoutItemWithPriceActual() {
            // given
            MonetaryAmount monetaryAmount = MonetaryFactory.pounds(0);
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0);

            // when
            DiscountCheckoutItem discountCheckoutItem = DiscountCheckoutItem.fromPriceAndPromotion(monetaryAmount, rule);

            // assert
            assertThat(discountCheckoutItem)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("priceActual", monetaryAmount);

            assertThat(discountCheckoutItem.getPromotionsApplied()).hasSize(1).contains(rule);
        }
    }

    @Nested
    @DisplayName("matchesItemId()")
    class MatchesItemId {

        @Test
        @DisplayName("Should return false")
        void shouldReturnFalse() {
            // given
            MonetaryAmount monetaryAmount = MonetaryFactory.pounds(0);
            TotalPercentagePromotionalRule promotionalRule = PromotionalRuleFactory.totalPercentage(0, 0);
            DiscountCheckoutItem discountCheckoutItem = DiscountCheckoutItem.fromPriceAndPromotion(monetaryAmount, promotionalRule);

            // when
            boolean result = discountCheckoutItem.matchesItemId(0);

            // assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("applyPromotion()")
    class ApplyPromotion {

        @Test
        @DisplayName("Given null amount: should throw NullPointerException")
        void givenNullAmount_shouldThrowNullPointerException() {
            // given
            MonetaryAmount monetaryAmount = MonetaryFactory.pounds(0);
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0);
            DiscountCheckoutItem discountCheckoutItem = DiscountCheckoutItem.fromPriceAndPromotion(monetaryAmount, rule);

            //throw
            assertThrows(NullPointerException.class, () -> discountCheckoutItem.applyPromotion(null, rule));
        }

        @Test
        @DisplayName("Given null promotion: should throw NullPointerException")
        void givenNullPromotion_shouldThrowNullPointerException() {
            // given
            MonetaryAmount monetaryAmount = MonetaryFactory.pounds(0);
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0, 0);
            DiscountCheckoutItem discountCheckoutItem = DiscountCheckoutItem.fromPriceAndPromotion(monetaryAmount, rule);

            //throw
            assertThrows(NullPointerException.class, () -> discountCheckoutItem.applyPromotion(monetaryAmount, null));
        }

        @Test
        @DisplayName("Given valid price and promotion, when price and promotion is retrieved: should match given price and promotion")
        void givenValidPriceAndPromotion_whenPriceAndPromotionIsRetrieved_shouldMatchGivenPriceAndPromotion() {
            // given
            MonetaryAmount originalMonetaryAmount = MonetaryFactory.pounds(0);
            TotalPercentagePromotionalRule originalRule = PromotionalRuleFactory.totalPercentage(0.0, 0f);
            DiscountCheckoutItem productCheckoutItem = DiscountCheckoutItem.fromPriceAndPromotion(originalMonetaryAmount, originalRule);

            MonetaryAmount updatedMonetaryAmount = MonetaryFactory.pounds(1);
            TotalPercentagePromotionalRule updatedRule = PromotionalRuleFactory.totalPercentage(1.0, 1f);

            // when
            productCheckoutItem.applyPromotion(updatedMonetaryAmount, updatedRule);

            // assert
            assertThat(productCheckoutItem.getPriceActual()).isEqualTo(updatedMonetaryAmount);
            assertThat(productCheckoutItem.getPromotionsApplied()).hasSize(2).contains(originalRule).contains(updatedRule);
        }
    }
}