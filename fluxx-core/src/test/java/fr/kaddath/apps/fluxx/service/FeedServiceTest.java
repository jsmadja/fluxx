package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;

public class FeedServiceTest extends AbstractTest {

    @Test
    public void testFindFeedsByInError() throws Exception {
        Feed feed = createFeed(FEED_CASTCODEURS);
        feed.setInError(true);
        feedService.update(feed);
        List<Feed> feeds = feedService.findFeedsByInError(true, 0);
        assertFalse(feeds.isEmpty());
        for (Feed f:feeds) {
            assertTrue(f.getInError());
        }
    }

    @Test
    public void testFindAllFeeds() throws Exception {
        createFeed(FEED_CASTCODEURS);
        createFeed(FEED_FRANDROID);

        List<Feed> feeds = feedService.findAllFeeds();
        assertNotNull(feeds);
        assertEquals(2, feeds.size());
    }

    @Test
    public void testFindAllFeedsNotInError() throws Exception {
        createFeed(FEED_FRANDROID);
        List<Feed> feeds = feedService.findAllFeedsNotInError();
        assertFalse(feeds.isEmpty());
        for (Feed feed:feeds) {
            assertFalse(feed.getInError());
        }
    }

    @Test
    public void testFindAllFeedsInError() throws Exception {
        Feed feed = createFeed();
        feed.setInError(true);
        feedService.update(feed);
        List<Feed> feeds = feedService.findAllFeedsInError();
        assertFalse(feeds.isEmpty());
        for (Feed f:feeds) {
            assertTrue(f.getInError());
        }
    }

    @Test
    public void testFindAvailableFeedsByAggregatedFeed() throws Exception {
        createFeed(FEED_CASTCODEURS);
        createFeed(FEED_FRANDROID);
        AggregatedFeed aggregatedFeed = createAggregatedFeedWithFeed(FEED_CASTCODEURS);
        List<Feed> feeds = feedService.findAvailableFeedsByAggregatedFeed(aggregatedFeed);
        assertEquals(1, feeds.size());
        assertEquals(FEED_FRANDROID, feeds.get(0).getUrl());
    }

    @Test
    public void testFindAvailableFeedsByAggregatedFeedWithFilter() throws Exception {
        createFeed(FEED_CASTCODEURS);
        createFeed(FEED_FRANDROID);
        AggregatedFeed aggregatedFeed = createAggregatedFeedWithFeed(FEED_CASTCODEURS);
        List<Feed> feeds = feedService.findAvailableFeedsByAggregatedFeedWithFilter(aggregatedFeed, "android");
        assertEquals(1, feeds.size());
        assertEquals(FEED_FRANDROID, feeds.get(0).getUrl());
    }

    @Test
    public void testFindFeedsByItemWithLike() throws Exception {
        createFeed(FEED_FRANDROID);
        List<Feed> feeds = feedService.findFeedsByItemWithLike("android");
        assertFalse(feeds.isEmpty());
    }

    @Test
    public void testFindFeedsByCategoryWithLike() throws Exception {
        createFeed(FEED_FRANDROID);
        List<Feed> feeds = feedService.findFeedsByCategoryWithLike("android");
        assertFalse(feeds.isEmpty());
    }

    @Test
    public void testFindFeedsWithLike() throws Exception {
        createFeed(FEED_FRANDROID);
        List<Feed> feeds = feedService.findFeedsWithLike("android");
        assertFalse(feeds.isEmpty());
    }

    @Test
    public void testGetNumFeeds() throws Exception {
        createFeed(FEED_FRANDROID);
        createFeed(FEED_CASTCODEURS);
        Long numFeeds = feedService.getNumFeeds();
        assertTrue(numFeeds > 0);
    }

    @Test
    public void testFindCategoriesByFeedId() throws Exception {
        Feed feed = createFeed();
        List<String> categories = feedService.findCategoriesByFeedId(feed.getId().toString());
        assertFalse(categories.isEmpty());
    }

    @Test
    public void testFindFeedById() throws Exception {
        Feed feed1 = createFeed();
        Feed feed2 = feedService.findFeedById(feed1.getId());
        assertEquals(feed1, feed2);
    }

    @Test
    public void testFindFeedByUrl() throws Exception {
        Feed feed1 = createFeed();
        Feed feed2 = feedService.findFeedByUrl(feed1.getUrl());
        assertEquals(feed1, feed2);
    }

    @Test
    public void testDelete() throws Exception {
        Feed feed = createFeed();
        Long numFeedsBefore = feedService.getNumFeeds();
        feedService.delete(feed);
        Long numFeedsAfter = feedService.getNumFeeds();
        assertTrue(numFeedsBefore > numFeedsAfter);
    }

    @Test
    public void testGetNumFeedType() throws Exception {
        createFeed();
        Map<String, Integer> feedType = feedService.getNumFeedType();
        assertFalse(feedType.isEmpty());

    }
}