package bidding;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Bid Tracking Service allows submission of a new Bid
 */

public class BidTrackerService implements BidTracker {
    /* compare and set lock for submission */
    private final Object submissionLock;

    /*
    * Map<K=Item, V=Ordered Map of Price/User>
    *
    * */

    private final Map<Item, ConcurrentSkipListMap<BigDecimal, User>> items;
    private final Comparator<BigDecimal> dscPriceComparator;
    private final ConcurrentHashMap<User, Set<Item>> userItems;

    public BidTrackerService() {
        this.submissionLock = new Object();
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
        /* check bid is valid */
        if(bid == null) {
            return false;
        } else if(bid.getItem() == null || bid.getPrice() == null || bid.getUser() == null) {
            return false;
        }

        Item item = bid.getItem();
        User user = bid.getUser();
        BigDecimal price = bid.getPrice();

        ConcurrentSkipListMap<BigDecimal, User> itemPriceMap;

        /* atomic compare and set logic */
        synchronized (submissionLock) {
            if(items.containsKey(item)) {
                itemPriceMap = items.get(item);
                BigDecimal winningPrice = itemPriceMap.firstKey();
                if(price.compareTo(winningPrice) < 0) {
                    /* bid is not higher than top price so reject */
                    return false;
                }
            } else {
                itemPriceMap = new ConcurrentSkipListMap<BigDecimal, User>(dscPriceComparator);
                items.put(item, itemPriceMap);
            }
            itemPriceMap.put(price, user);

            Set<Item> myItems = userItems.get(user);

            // keep a separate store of items per user
            if(myItems == null) {
                myItems = Collections.newSetFromMap(new ConcurrentHashMap<Item, Boolean>());
                userItems.put(user, myItems);
            }
            myItems.add(item);
        }
        return true;
    }

    @Override
    public BigDecimal getWinningBid(Item item) {
        if(item == null) {
            return null;
        }

        ConcurrentSkipListMap<BigDecimal, User> itemPriceMap = items.get(item);
        /* grab the top price */
        if(itemPriceMap == null) {
            return null;
        }
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

        return Collections.unmodifiableSet(itemPriceMap.keySet());
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

        return Collections.unmodifiableSet(allItems);
    }
}
