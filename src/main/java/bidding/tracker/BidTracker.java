package bidding.tracker;

import bidding.types.Bid;
import bidding.types.Item;
import bidding.types.User;

import java.math.BigDecimal;

import java.util.Set;

/**
 * BidImpl Tracker interface
 */
public interface BidTracker {
    // true for successful bid, false otherwise
    boolean submit(Bid bid);

    // get the winning Bid for an item
    BigDecimal getWinningBid(Item item);

    // get a set of bids for an item
    Set<BigDecimal> getAllBids(Item item);

    // get a set of all items a user has a bid on
    Set<Item> getAllItems(User user);
}
