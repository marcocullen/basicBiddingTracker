package bidding;

import java.math.BigDecimal;

/**
 * Created by marco on 28/03/14.
 */
public class Bid {
    private final String itemName;
    private final String userName;
    private final BigDecimal price;

    public String getItemName() {
        return itemName;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Bid(String username, String itemName, BigDecimal price) {
        this.itemName = itemName;
        this.userName = username;
        this.price = price;
    }
}
