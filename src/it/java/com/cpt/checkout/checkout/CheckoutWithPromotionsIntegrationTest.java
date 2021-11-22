package com.cpt.checkout.checkout;

import com.cpt.checkout.item.Item;
import com.cpt.checkout.item.ItemFactory;
import com.cpt.checkout.monetary.MonetaryFactory;
import com.cpt.checkout.promotion.PromotionalRuleFactory;
import com.cpt.checkout.promotion.checkout.TotalPercentagePromotionalRule;
import com.cpt.checkout.promotion.item.FixedPricePromotionalRule;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class CheckoutWithPromotionsIntegrationTest {

    public static Stream<Arguments> idTotalArgumentStream() {
        return Stream.of(
                Arguments.of(emptyList(), 0),
                Arguments.of(singletonList(1), 24.95),
                Arguments.of(singletonList(2), 65.0),
                Arguments.of(singletonList(3), 3.99),
                Arguments.of(asList(1, 1, 2, 3), 103.47),
                Arguments.of(asList(2, 1, 3, 1), 103.47),
                Arguments.of(asList(1, 1, 1), 68.97),
                Arguments.of(asList(2, 2, 3), 120.59),
                Arguments.of(asList(2, 3, 2), 120.59)
        );
    }

    @ParameterizedTest
    @MethodSource("idTotalArgumentStream")
    @DisplayName("Given list of IDs: total() should return correct amount")
    void givenListOfIds_TotalShouldReturnCorrectAmount(List<Integer> ids, double totalPounds) {
        // given
        FixedPricePromotionalRule fixedPriceRule = PromotionalRuleFactory.fixedPrice(1, 2, 22.99, 1);
        TotalPercentagePromotionalRule totalPercentageRule = PromotionalRuleFactory.totalPercentage(75.0, 10.0f, 10);
        CheckoutWithPromotions checkoutWithPromotions = new CheckoutWithPromotions(ImmutableSet.of(fixedPriceRule, totalPercentageRule), MonetaryFactory.poundsCurrency());

        List<Item> items = ids.stream()
                .map(ItemFactory::forId)
                .collect(Collectors.toList());

        // when
        checkoutWithPromotions.scanItems(items);
        MonetaryAmount result = checkoutWithPromotions.total();

        assertThat(result).isEqualTo(MonetaryFactory.pounds(totalPounds));
    }

}