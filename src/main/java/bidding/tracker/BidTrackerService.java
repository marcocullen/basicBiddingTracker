package bidding.tracker;

import bidding.types.Bid;
import bidding.types.Item;
import bidding.types.User;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * BidImpl Tracking Service
 *
 * Uses a ConcurrentHashMap of Items->[Bid Price - User] as its base structure
 * each item that is bid upon becomes a key in the items map
 * the value associated to each item is a descending
 * concurrentSkipListMap of price-user entries.
 * first entry is winning price entered by User
 *
 * userItems is a map of users and a set of the items they have a bid on
 */

public class BidTrackerService implements BidTracker {
    // Map<K=ItemImpl, V=Ordered Map of Price/User>
    private final ConcurrentMap<Item, ConcurrentSkipListMap<BigDecimal, User>> items;
    private final ConcurrentMap<User, Set<Item>> userItems;
    private final Comparator<BigDecimal> dscPriceComparator;

    public BidTrackerService() {
        items = new ConcurrentHashMap<Item, ConcurrentSkipListMap<BigDecimal, User>>();

        /* want prices in descending order so constant time for top price grab */
        dscPriceComparator = new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return o2.compareTo(o1);
            }
        };
        userItems = new ConcurrentHashMap<User, Set<Item>>();
    }

    @Override
    public boolean submit(Bid bid) {
        /* basic check bid is valid */
        if(bid == null) {
            return false;
        } else if(bid.getItem() == null || bid.getPrice() == null || bid.getUser() == null) {
            return false;
        }

        Item item = bid.getItem();
        User user = bid.getUser();
        BigDecimal price = bid.getPrice();

        /* for each item create a list of descending price/user mappings */
        items.putIfAbsent(item, new ConcurrentSkipListMap<BigDecimal, User>(dscPriceComparator));
        ConcurrentSkipListMap<BigDecimal, User> itemPriceMap = items.get(item);

        BigDecimal  winningPrice;

        if(itemPriceMap.isEmpty()) {
            winningPrice = new BigDecimal(-1);
        } else {
            winningPrice = itemPriceMap.firstKey();
        }

        if(price.compareTo(winningPrice) < 0) {
            return false;
        } else {
            /* add bid [the new winning bid], at head node, constant time */
            itemPriceMap.put(price, user);
        }

        userItems.putIfAbsent(user, Collections.newSetFromMap(new ConcurrentHashMap<Item, Boolean>()));
        userItems.get(user).add(item);

        return true;
    }

    @Override
    public BigDecimal getWinningBid(Item item) {
        if(item == null) {
            return null;
        }

        ConcurrentSkipListMap<BigDecimal, User> itemPriceMap = items.get(item);

        if(itemPriceMap == null) {
            return null;
        }
        /* grab the top price constant time */
        return itemPriceMap.firstKey();
    }

    @Override
    public Set<BigDecimal> getAllBids(Item item) {
        if(item == null) {
            return null;
        }

        ConcurrentSkipListMap<BigDecimal, User> itemPriceMap = items.get(item);

        if(itemPriceMap == null) {
            return null;
        }
        /* return all bids for item */
        Set<BigDecimal> bids = itemPriceMap.keySet();
        return new HashSet<BigDecimal>(bids);
    }

    @Override
    public Set<Item> getAllItems(User user) {
        if(user == null) {
            return null;
        }

        Set<Item> allItems = userItems.get(user);

        if(allItems == null) {
            return null;
        }
        /* return all items for user */
        return new HashSet<Item>(allItems);
    }
}
