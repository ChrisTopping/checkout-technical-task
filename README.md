# Checkout technical task

An eCommerce checkout implementation for an interview technical task

### Brief:

Implement a checkout that is able to apply the following rules:

1. If you spend over £75 then you get a 10% discount
2. If you buy two or more water bottles, then the price for each drops to £22.99
3. Multiple promotions can be applied to the same checkout
4. The checkout system should be able to scan the items in any order then apply the promotion rules
5. The promotion rules should be allowed to change over time

### Usage:

#### Checkout

The `Checkout` interface has a single implementation; `CheckoutWithPromotions`.  To calculate a checkout instance using `CheckoutWithPromotions` first initialise it with a set of desired promotions, and the desired currency for the transaction.

```java
final Set<PromotionalRule> promotionalRules = ...;
final CurrencyUnit currencyUnit = ...;
CheckoutWithPromotions checkout = new CheckoutWithPromotions(promotionRules, currencyunit);
```

where `currencyUnit` has the type of `javax.money.CurrencyUnit`.

The set of `PromotionalRule`s may be provided without a specific order.

Calculate the checkout using the `scanItems()` method.

```java
final List<Item> items = ...;
checkout.scanItems(items);
```

To calculate the total cost of the checkout, call the `total()` method.

For brevity, all validation has been performed using guava's `check...()` methods. For a production library it would be preferable to implement custom exceptions (and possibly use a validation framework like javax.validation).

```java
MonetaryAmount total = checkout.total();
```
#### Items vs Checkout Items

The distinction between `Item` and `CheckoutItem` is made to segregate the concept of an immutable 'item' as it would be stored in a database from the current instance of an item in the customer's checkout. This enables the application of discounts on specific items.

#### Money

All monetary value is represented using the [`javax.money`](https://javamoney.github.io/apidocs/javax/money/package-summary.html) library.

The `CurrencyUnit` must be provided to the `CheckoutWithPromotions` class upon initialisation so that in the event of a default value for an empty checkout, the currency can be derived to return the `MonetaryAmount` 

#### Promotions

There are two types of promotions that can be applied at checkout:
1. Promotions that apply to an individual/group of items
2. Promotions that apply to the basket as a whole

It follows that promotions that apply to a basket should be applied after all individual/group promotions. To allow for this, each `PromotionalRule` implementation must implement a `int getPriority()` method. The `PromotionalRule` interface extends `Comparable<PromotionalRule>`, and the default `compareTo()` method utilises the `getPriority()` result to allow `PromotionalRule`s to be sorted naturally.

Where there is a definitive order in which promotions should be applied, rules that should be applied sooner should have smaller priorities.

E.g. in the brief, the 10% discount for orders above £75 should be applied after the fixed price water bottle discount, and so should have a higher priority.

In the case that there are a great number of concurrent promotions, the integer-typed `priority` might become cumbersome, as priorities might have to be modified in order to fit new promotions between adjacent existing promotions. Float- or double-typed priorities would resolve this. 

There are two implementations of `PromotionalRule`:

1`FixedPricePromotionalRule`: sets the cost of affected items to a fixed value if there are at least a specified threshold number of them in the checkout.
2`TotalPercentagePromotionalRule`: applies a percent discount to the checkout when a total checkout threshold has been reached.

These work in a notably different way:

1. `FixedPricePromotionalRule` mutates the affected `ProductCheckoutItem`s by setting a new `priceActual` based on the rules of the promotion.
2. `TotalPercentagePromotionalRule` creates a new `DiscountCheckoutItem` object which represents a reduction in the overall cost of the checkout.

This has the benefit that each `CheckoutItem` acting like a line-item from a receipt/invoice, and could be used to drive front-end in displaying breakdown of costs etc.

The `PromotionalRule` interface is designed to be extensible, and implementing a new rule is straightforward:
1. Ensure that the `apply()` method conditionally applies where appropriate, rather than every time it is called.
2. Apply the promotion by either:
   1. Mutating the price of affected items.
   2. Adding a new `CheckoutItem` to the `checkoutItems` parameter. This can be either: 
      1. `DiscountCheckoutItem` (for discounting the total cost of the checkout) or 
      2. `ProductCheckoutItem` (e.g. for free-gift promotions).

Additionally, a promotional rule can query the existing promotional rules that have been applied to the provided checkout items as part of their conditional check.

### Build/Test

The project uses jacoco to verify test coverage and has 100% coverage.

To build the project, run the following gradle command:

```groovy
./gradlew clean build
```

This also runs all unit and integration tests.

To run unit tests on their own:

```groovy
./gradlew clean test
```

To run integration tests on their own:

```groovy
./gradlew clean integrationTest
```