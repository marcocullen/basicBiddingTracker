package bidding;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Bid Tracking Service allows submission of a new Bid
 */

public class BidTrackerService implements BidTracker {
    /* compare and set lock for submission */
    private final Object submissionLock;

    /*
    * Map<K=Item, V=Ordered Map of Price/User>
    * */

    private final Map<String, ConcurrentSkipListMap<BigDecimal, String>> items;
    private final Comparator<BigDecimal> dscPriceComparator;
    private final ConcurrentMap<String, Set<String>> userItems;

    public BidTrackerService() {
        this.submissionLock = new Object();
        items = new ConcurrentHashMap<String, ConcurrentSkipListMap<BigDecimal, String>>();

        /* want prices in descending order so constant time for top price grab */
        dscPriceComparator = new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return o2.compareTo(o1);
            }
        };
        userItems = new ConcurrentHashMap<String, Set<String>>();
    }

    @Override
    public boolean submit(Bid bid) {
        String itemName = bid.getItemName();
        String username = bid.getUserName();
        BigDecimal price = bid.getPrice();

        ConcurrentSkipListMap<BigDecimal, String> itemPriceMap;

        /*atomic compare and set */
        synchronized (submissionLock) {
            if(items.containsKey(itemName)) {
                itemPriceMap = items.get(itemName);
                BigDecimal winningPrice = itemPriceMap.firstKey();
                if(price.compareTo(winningPrice) < 0) {
                    /* bid is not higher than top price */
                    return false;
                }
            } else {
                itemPriceMap = new ConcurrentSkipListMap<BigDecimal, String>(dscPriceComparator);
                items.put(itemName, itemPriceMap);
            }
            itemPriceMap.put(price, username);

            Set<String> myItems = userItems.get(username);

            if(myItems == null) {
                myItems = new HashSet<String>();
                userItems.put(username, myItems);
            }
            myItems.add(itemName);
        }
        return true;
    }

    @Override
    public BigDecimal getWinningBid(String item) {
        ConcurrentSkipListMap<BigDecimal, String> itemPriceMap = items.get(item);
        /* grab the top price */
        if(itemPriceMap == null) {
            return null;
        }
        return itemPriceMap.firstKey();
    }

    @Override
    public Set<BigDecimal> getAllBids(String item) {
        ConcurrentSkipListMap<BigDecimal, String> itemPriceMap;
        itemPriceMap = items.get(item);

        if(itemPriceMap == null) {
            return null;
        }
        return itemPriceMap.keySet();
    }

    @Override
    public Set<String> getAllItems(String username) {
        return userItems.get(username);
    }
}
