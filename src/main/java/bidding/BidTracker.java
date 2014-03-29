package bidding;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by marco on 28/03/14.
 */
public interface BidTracker {
    boolean submit(Bid bid);
    BigDecimal getWinningBid(String item);
    Set<BigDecimal> getAllBids(String item);
    Set<String> getAllItems(String username);
}
