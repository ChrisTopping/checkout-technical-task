package com.cpt.checkout.checkout_item;

import com.cpt.checkout.item.Item;
import com.cpt.checkout.item.ItemFactory;
import com.cpt.checkout.monetary.MonetaryFactory;
import com.cpt.checkout.promotion.PromotionalRuleFactory;
import com.cpt.checkout.promotion.checkout.TotalPercentagePromotionalRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductCheckoutItemTest {

    @Nested
    @DisplayName("fromItem()")
    class FromItem {

        @Test
        @DisplayName("Given null Item: should throw NullPointerException")
        void givenNullItem_shouldThrowNullPointerException() {
            // throw
            assertThrows(NullPointerException.class, () -> ProductCheckoutItem.fromItem(null));
        }

        @Test
        @DisplayName("Given non-null Item: should return ProductCheckoutItem with base price")
        void givenNonNullItem_shouldReturnProductCheckoutItemWithBasePrice() {
            // given
            Item item = ItemFactory.waterBottle();

            // when
            ProductCheckoutItem productCheckoutItem = ProductCheckoutItem.fromItem(item);

            // assert
            assertThat(productCheckoutItem)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("item", item)
                    .hasFieldOrPropertyWithValue("priceActual", item.getBasePrice());

            assertThat(productCheckoutItem.getPromotionsApplied()).isEmpty();
        }
    }

    @Nested
    @DisplayName("matchesItemId()")
    class MatchesItemId {

        @Test
        @DisplayName("Given ID does not match: should return false")
        void givenIdDoesNotMatch_shouldReturnFalse() {
            // given
            Item item = ItemFactory.waterBottle();
            ProductCheckoutItem productCheckoutItem = ProductCheckoutItem.fromItem(item);

            // when
            boolean result = productCheckoutItem.matchesItemId(item.getId() + 1);

            // assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Given ID matches: should return true")
        void givenIdMatches_shouldReturnTrue() {
            // given
            Item item = ItemFactory.waterBottle();
            ProductCheckoutItem productCheckoutItem = ProductCheckoutItem.fromItem(item);

            // when
            boolean result = productCheckoutItem.matchesItemId(item.getId());

            // assert
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("applyPromotion()")
    class ApplyPromotion {

        @Test
        void givenNullAmount_shouldThrowNullPointerException() {
            // given
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0.0, 0f);
            ProductCheckoutItem productCheckoutItem = ProductCheckoutItem.fromItem(ItemFactory.waterBottle());

            //throw
            assertThrows(NullPointerException.class, () -> productCheckoutItem.applyPromotion(null, rule));
        }

        @Test
        @DisplayName("Given null PromotionalRule: should throw NullPointerException")
        void givenNullPromotionalRule_shouldThrowNullPointerException() {
            // given
            MonetaryAmount monetaryAmount = MonetaryFactory.pounds(0);
            ProductCheckoutItem productCheckoutItem = ProductCheckoutItem.fromItem(ItemFactory.waterBottle());

            // throw
            assertThrows(NullPointerException.class, () -> productCheckoutItem.applyPromotion(monetaryAmount, null));
        }

        @Test
        @DisplayName("Given valid price and promotion, when price and promotion is retrieved: should match given price and promotion")
        void givenValidPriceAndPromotion_whenPriceAndPromotionIsRetrieved_shouldMatchGivenPriceAndPromotion() {
            // given
            MonetaryAmount monetaryAmount = MonetaryFactory.pounds(0);
            TotalPercentagePromotionalRule rule = PromotionalRuleFactory.totalPercentage(0.0, 0f);
            ProductCheckoutItem productCheckoutItem = ProductCheckoutItem.fromItem(ItemFactory.waterBottle());

            // when
            productCheckoutItem.applyPromotion(monetaryAmount, rule);

            // assert
            assertThat(productCheckoutItem.getPriceActual()).isEqualTo(monetaryAmount);
            assertThat(productCheckoutItem.getPromotionsApplied()).hasSize(1).contains(rule);
        }
    }

}