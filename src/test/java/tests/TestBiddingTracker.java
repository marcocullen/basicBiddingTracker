package tests;

import bidding.Bid;
import bidding.BidTracker;
import bidding.BidTrackerService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by marco on 28/03/14.
 */
public class TestBiddingTracker {
    BidTracker bidTracker;

    public TestBiddingTracker() {
        bidTracker = new BidTrackerService();
    }

    @Before
    public void setUp() throws Exception {
        Bid bid1 = new Bid("user1","watch", new BigDecimal(12.04));
        Bid bid2 = new Bid("user2","book", new BigDecimal(12.04));
        Bid bid3 = new Bid("user3","tv", new BigDecimal(12.04));
        Bid bid4 = new Bid("user4","watch", new BigDecimal(12.14));
        Bid bid5 = new Bid("user3","table", new BigDecimal(12.04));
        Bid bid6 = new Bid("user6","watch", new BigDecimal(13.04));

        bidTracker.submit(bid1);
        bidTracker.submit(bid2);
        bidTracker.submit(bid3);
        bidTracker.submit(bid4);
        bidTracker.submit(bid5);
        bidTracker.submit(bid6);
    }

    @Test
    public void testGetWinningBid() {
        assertEquals(new BigDecimal(13.04), bidTracker.getWinningBid("watch"));
    }

    @Test
    public void testSubmitFail() {
        Bid failBid = new Bid("user6", "watch", new BigDecimal(13.01));
        assertFalse(bidTracker.submit(failBid));
    }

    @Test
    public void testSubmitSuccess() {
        Bid successfulBid = new Bid("user10", "swimming pool", new BigDecimal(110.01));
        assertTrue(bidTracker.submit(successfulBid));
    }

    @Test
    public void testGetAllBids() {
        Set<BigDecimal> watchBids = new HashSet<BigDecimal>();

        watchBids.add(new BigDecimal(12.04));
        watchBids.add(new BigDecimal(12.14));
        watchBids.add(new BigDecimal(13.04));

        assertEquals(watchBids, bidTracker.getAllBids("watch"));
    }

    @Test
    public void testGetAllItems() {
        Set<String> myItems = new HashSet<String>();

        myItems.add("tv");
        myItems.add("table");

        assertEquals(myItems, bidTracker.getAllItems("user3"));
    }
}