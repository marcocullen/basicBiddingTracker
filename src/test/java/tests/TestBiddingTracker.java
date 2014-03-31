package tests;

import bidding.tracker.BidTracker;
import bidding.tracker.BidTrackerService;
import bidding.types.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * BidImpl Tracker unit tests
 */
public class TestBiddingTracker {
    BidTracker bidTracker;

    public TestBiddingTracker() {
        bidTracker = new BidTrackerService();
    }

    @Before
    public void setUp() throws Exception {
        Bid bid1 = new BidImpl(new UserImpl("user1"),new ItemImpl("watch"), new BigDecimal(12.04));
        Bid bid2 = new BidImpl(new UserImpl("user2"),new ItemImpl("book"), new BigDecimal(12.04));
        Bid bid3 = new BidImpl(new UserImpl("user3"),new ItemImpl("tv"), new BigDecimal(12.04));
        Bid bid4 = new BidImpl(new UserImpl("user4"),new ItemImpl("watch"), new BigDecimal(12.14));
        Bid bid5 = new BidImpl(new UserImpl("user3"),new ItemImpl("table"), new BigDecimal(12.04));
        Bid bid6 = new BidImpl(new UserImpl("user6"),new ItemImpl("watch"), new BigDecimal(13.04));

        bidTracker.submit(bid1);
        bidTracker.submit(bid2);
        bidTracker.submit(bid3);
        bidTracker.submit(bid4);
        bidTracker.submit(bid5);
        bidTracker.submit(bid6);
    }

    @Test
    public void testSubmitLowerPriceFail() {
        Bid failBid = new BidImpl(new UserImpl("user6"), new ItemImpl("watch"), new BigDecimal(13.01));
        assertFalse(bidTracker.submit(failBid));
    }

    @Test
    public void testSubmitSuccess() {
        Bid successfulBid = new BidImpl(new UserImpl("user10"), new ItemImpl("swimming pool"), new BigDecimal(110.01));
        assertTrue(bidTracker.submit(successfulBid));
    }

    @Test
    public void testSubmitNullBid() {
        assertFalse(bidTracker.submit(null));
    }

    @Test
    public void testSubmitBadBidNullItem() {
        assertFalse(bidTracker.submit(new BidImpl(new UserImpl("name"), null, new BigDecimal("09.02"))));
    }

    @Test
    public void testSubmitBadBidNullUser() {
        assertFalse(bidTracker.submit(new BidImpl(null, new ItemImpl("swimming pool"), new BigDecimal("09.02"))));
    }

    @Test
    public void testSubmitBadBidNullPrice() {
        assertFalse(bidTracker.submit(new BidImpl(null, new ItemImpl("swimming pool"), null)));
    }

    @Test
    public void testGetWinningBid() {
        assertEquals(new BigDecimal(13.04), bidTracker.getWinningBid(new ItemImpl("watch")));
    }

    @Test
    public void testGetAllBids() {
        Set<BigDecimal> watchBids = new HashSet<BigDecimal>();

        watchBids.add(new BigDecimal(12.04));
        watchBids.add(new BigDecimal(12.14));
        watchBids.add(new BigDecimal(13.04));

        assertEquals(watchBids, bidTracker.getAllBids(new ItemImpl("watch")));
    }

    @Test
    public void testGetAllItems() {
        Set<Item> myItems = new HashSet<Item>();

        myItems.add(new ItemImpl("tv"));
        myItems.add(new ItemImpl("table"));

        assertEquals(myItems, bidTracker.getAllItems(new UserImpl("user3")));
    }

    @Test
    public void testGetAllItemsNull() {
        assertNull(bidTracker.getAllItems(null));
    }

    @Test
    public void testGetAllItemsUserNoExist() {
        assertNull(bidTracker.getAllItems(new UserImpl("user12")));
    }

    @Test
    public void testGetAllBidsItemNull() {
        assertNull(bidTracker.getAllBids(null));
    }

    @Test
    public void testGetAllBidsItemNoExist() {
        assertNull(bidTracker.getAllBids(new ItemImpl("NonExistentItem")));
    }
}