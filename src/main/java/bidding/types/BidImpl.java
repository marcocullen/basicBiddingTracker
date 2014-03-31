package bidding.types;

import java.math.BigDecimal;

/**
 * basic implementation of Bid
 */
public class BidImpl implements Bid {
    private final Item item;
    private final User user;
    private final BigDecimal price;

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public BidImpl(User user, Item item, BigDecimal price) {
        this.item = item;
        this.user = user;
        this.price = price;
    }
}
