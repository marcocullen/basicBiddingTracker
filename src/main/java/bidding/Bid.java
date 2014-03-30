package bidding;

import java.math.BigDecimal;

/**
 * Basic Bid Class,
 * A bid is a composite of an item, a user and a price
 * no equals/hashcode overriding as we only use this as the delivery object
 */
public class Bid {
    private final Item item;
    private final User user;
    private final BigDecimal price;

    public Item getItem() {
        return item;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Bid(User user, Item item, BigDecimal price) {
        this.item = item;
        this.user = user;
        this.price = price;
    }
}
