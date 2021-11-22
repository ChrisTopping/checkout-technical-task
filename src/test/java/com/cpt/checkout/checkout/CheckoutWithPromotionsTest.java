package com.cpt.checkout.checkout;

import com.cpt.checkout.checkout_item.CheckoutItem;
import com.cpt.checkout.item.Item;
import com.cpt.checkout.item.ItemFactory;
import com.cpt.checkout.promotion.PromotionalRule;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutWithPromotionsTest {

    @Mock
    private PromotionalRule promotionalRuleMock1;

    @Mock
    private PromotionalRule promotionalRuleMock2;

    @Spy
    private Set<PromotionalRule> promotionalRuleSetSpy = new HashSet<>();

    @Mock
    private CurrencyUnit currencyUnitMock;

    @InjectMocks
    private CheckoutWithPromotions checkoutWithPromotions;

    @BeforeEach
    public void setUpPromotionalRules() {
        promotionalRuleSetSpy.addAll(asList(promotionalRuleMock1, promotionalRuleMock2));
    }

    @Nested
    @DisplayName("scanItems()")
    class ScanItems {

        @Test
        @DisplayName("Given scanItems() has not been called: getCheckoutItems() should return empty list")
        void givenScanItemsHasNotBeenCalled_GetCheckoutItemsShouldReturnEmptyList() {
            // assert
            List<CheckoutItem> checkoutItems = checkoutWithPromotions.getCheckoutItems();
            assertThat(checkoutItems).isEmpty();
        }

        @Test
        @DisplayName("Given empty list: should not invoke rules")
        void givenEmptyList_ShouldNotInvokeRules() {
            // when
            checkoutWithPromotions.scanItems(emptyList());

            // assert
            List<CheckoutItem> checkoutItems = checkoutWithPromotions.getCheckoutItems();
            assertThat(checkoutItems).isEmpty();

            // verify
            verifyNoInteractions(promotionalRuleMock1, promotionalRuleMock2);
        }

        @Test
        @DisplayName("Given non-empty list: rules should be applied to items in priority order")
        void givenNonEmptyList_RulesShouldBeAppliedToItemsInPriorityOrder() {
            // given
            List<Item> items = asList(ItemFactory.stickerSet(), ItemFactory.hoodie());

            lenient().doReturn(-1).when(promotionalRuleMock1).compareTo(any(PromotionalRule.class));
            lenient().doReturn(1).when(promotionalRuleMock2).compareTo(any(PromotionalRule.class));

            InOrder inOrder = inOrder(promotionalRuleMock1, promotionalRuleMock2);

            // when
            checkoutWithPromotions.scanItems(items);

            // assert
            List<CheckoutItem> checkoutItems = checkoutWithPromotions.getCheckoutItems();
            assertThat(checkoutItems)
                    .hasSize(2)
                    .anyMatch(checkoutItem -> checkoutItem.matchesItemId(2))
                    .anyMatch(checkoutItem -> checkoutItem.matchesItemId(3));

            // verify
            inOrder.verify(promotionalRuleMock1).apply(argThat(argument -> argument.size() == 2));
            inOrder.verify(promotionalRuleMock2).apply(argThat(argument -> argument.size() == 2));
        }

    }

    @Nested
    @DisplayName("total()")
    class Total {

        @Test
        @DisplayName("Given checkoutItems is empty: should return zero")
        void givenCheckoutItemsIsEmpty_ShouldReturnZero() {
            // when
            MonetaryAmount result = checkoutWithPromotions.total();

            // assert
            assertThat(result).isEqualTo(Money.of(0, currencyUnitMock));
        }

        @Test
        @DisplayName("Given checkoutItems is not empty: should return sums of priceActuals")
        void givenCheckoutItemsIsNotEmpty_ShouldReturnSumsOfPriceActuals() {
            // given
            List<Item> items = asList(ItemFactory.stickerSet(), ItemFactory.hoodie());
            checkoutWithPromotions.scanItems(items);

            // when
            MonetaryAmount result = checkoutWithPromotions.total();

            MonetaryAmount expectedTotal = Stream.of(ItemFactory.hoodie(), ItemFactory.stickerSet())
                    .map(Item::getBasePrice)
                    .reduce(MonetaryFunctions::sum)
                    .get();

            // assert
            assertThat(result).isEqualTo(expectedTotal);
        }
    }
}