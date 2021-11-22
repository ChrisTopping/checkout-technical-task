package com.cpt.checkout.item;

import com.cpt.checkout.monetary.MonetaryFactory;

import java.text.MessageFormat;

public class ItemFactory {

    public static Item item(long id, String name, double amountPounds) {
        return new Item(id, name, MonetaryFactory.pounds(amountPounds));
    }

    public static Item forId(long id) {
        return switch ((int) id) {
            case 1 -> waterBottle();
            case 2 -> hoodie();
            case 3 -> stickerSet();
            default -> throw new IllegalArgumentException(MessageFormat.format("Item with id {0} does not exist", id));
        };
    }

    public static Item waterBottle() {
        return new Item(1, "Water Bottle", MonetaryFactory.pounds(24.95));
    }

    public static Item hoodie() {
        return new Item(2, "Hoodie", MonetaryFactory.pounds(65.0));
    }

    public static Item stickerSet() {
        return new Item(3, "Sticker Set", MonetaryFactory.pounds(3.99));
    }
}
