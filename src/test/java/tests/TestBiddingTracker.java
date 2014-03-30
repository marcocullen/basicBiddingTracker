package tests;

import bidding.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Bid Tracker unit tests
 */
public class TestBiddingTracker {
    BidTracker bidTracker;

    public TestBiddingTracker() {
        bidTracker = new BidTrackerService();
    }

    @Before
    public void setUp() throws Exception {
        Bid bid1 = new Bid(new User("user1"),new Item("watch"), new BigDecimal(12.04));
        Bid bid2 = new Bid(new User("user2"),new Item("book"), new BigDecimal(12.04));
        Bid bid3 = new Bid(new User("user3"),new Item("tv"), new BigDecimal(12.04));
        Bid bid4 = new Bid(new User("user4"),new Item("watch"), new BigDecimal(12.14));
        Bid bid5 = new Bid(new User("user3"),new Item("table"), new BigDecimal(12.04));
        Bid bid6 = new Bid(new User("user6"),new Item("watch"), new BigDecimal(13.04));

        bidTracker.submit(bid1);
        bidTracker.submit(bid2);
        bidTracker.submit(bid3);
        bidTracker.submit(bid4);
        bidTracker.submit(bid5);
        bidTracker.submit(bid6);
    }

    @Test
    public void testSubmitFail() {
        Bid failBid = new Bid(new User("user6"), new Item("watch"), new BigDecimal(13.01));
        assertFalse(bidTracker.submit(failBid));
    }

    @Test
    public void testSubmitSuccess() {
        Bid successfulBid = new Bid(new User("user10"), new Item("swimming pool"), new BigDecimal(110.01));
        assertTrue(bidTracker.submit(successfulBid));
    }

    @Test
    public void testSubmitNullBid() {
        assertFalse(bidTracker.submit(null));
    }

    @Test
    public void testSubmitBadBidNullItem() {
        assertFalse(bidTracker.submit(new Bid(new User("name"), null, new BigDecimal("09.02"))));
    }

    @Test
    public void testSubmitBadBidNullUser() {
        assertFalse(bidTracker.submit(new Bid(null, new Item("swimming pool"), new BigDecimal("09.02"))));
    }

    @Test
    public void testSubmitBadBidNullPrice() {
        assertFalse(bidTracker.submit(new Bid(null, new Item("swimming pool"), null)));
    }

    @Test
    public void testGetWinningBid() {
        assertEquals(new BigDecimal(13.04), bidTracker.getWinningBid(new Item("watch")));
    }

    @Test
    public void testGetAllBids() {
        Set<BigDecimal> watchBids = new HashSet<BigDecimal>();

        watchBids.add(new BigDecimal(12.04));
        watchBids.add(new BigDecimal(12.14));
        watchBids.add(new BigDecimal(13.04));

        assertEquals(watchBids, bidTracker.getAllBids(new Item("watch")));
    }

    @Test
    public void testGetAllItems() {
        Set<Item> myItems = new HashSet<Item>();

        myItems.add(new Item("tv"));
        myItems.add(new Item("table"));

        assertEquals(myItems, bidTracker.getAllItems(new User("user3")));
    }

    @Test
    public void testGetAllItemsNull() {
        assertNull(bidTracker.getAllItems(null));
    }

    @Test
    public void testGetAllItemsUserNoExist() {
        assertNull(bidTracker.getAllItems(new User("user12")));
    }

    @Test
    public void testGetAllBidsItemNull() {
        assertNull(bidTracker.getAllBids(null));
    }

    @Test
    public void testGetAllBidsItemNoExist() {
        assertNull(bidTracker.getAllBids(new Item("NonExistentItem")));
    }
}