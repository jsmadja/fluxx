package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.domain.Item;

public class AggregatedFeedServiceTest extends AbstractTest {

    private static final Logger log = Logger.getLogger(AggregatedFeedServiceTest.class.getName());

    private Fluxxer fluxxer;
    
    @Before
    public void setUp() {
        fluxxer = createFluxxer();
        log.log(Level.INFO, "fluxxer {0}", fluxxer.getUsername());
    }
    
    @Test
    public void addAggregatedFeed() {
        Long old = aggregatedFeedService.getNumAggregatedFeeds();
        log.log(Level.INFO, "{0} aggregatedfeeds before adding", old);
        aggregatedFeedService.addAggregatedFeed(fluxxer, createRandomString(), 7);
        log.log(Level.INFO, "{0} aggregatedfeeds after adding", aggregatedFeedService.getNumAggregatedFeeds());
        assertTrue(aggregatedFeedService.getNumAggregatedFeeds() > old);
    }

    @Test
    public void testCreateUrl() throws Exception {
        fluxxer = addRandomAggregatedFeed(fluxxer);
        AggregatedFeed feed = fluxxer.getAggregatedFeeds().get(0);
        String expResult = "http://"+serverName+":"+serverPort+contextPath+"/rss?id="+feed.getAggregatedFeedId();
        String result = aggregatedFeedService.createUrl(request, feed);
        log.info(result);
        assertEquals(expResult, result);
    }

    @Test
    public void findItemsByAggregatedFeed() {
        fluxxer = addRandomAggregatedFeed(fluxxer);
        List<Feed> feeds = new ArrayList<Feed>();
        feeds.add(createFeed());
        AggregatedFeed aggregatedFeed = fluxxer.getAggregatedFeeds().get(0);
        aggregatedFeed.setFeeds(feeds);
        aggregatedFeed = aggregatedFeedService.merge(aggregatedFeed);
        List<Item> items = aggregatedFeedService.findItemsByAggregatedFeed(aggregatedFeed);
        assertTrue(items.size()>0);        
    }

    @Test
    public void findByAggregatedFeedId() {
        fluxxer = aggregatedFeedService.addAggregatedFeed(fluxxer, createRandomString(), 7);
        List<AggregatedFeed> aggregatedFeeds = fluxxer.getAggregatedFeeds();
        assertFalse(aggregatedFeeds.isEmpty());
        for (AggregatedFeed af:aggregatedFeeds) {
            assertNotNull(aggregatedFeedService.findByAggregatedFeedId(af.getAggregatedFeedId()));
        }
    }

    @Test
    public void delete() {
        fluxxer = addRandomAggregatedFeed(fluxxer);
        fluxxer = addRandomAggregatedFeed(fluxxer);
        Long old = aggregatedFeedService.getNumAggregatedFeeds();
        log.log(Level.INFO, "{0} aggregatedfeeds before deleting", old);
        List<AggregatedFeed> aggregatedFeeds = fluxxer.getAggregatedFeeds();
        assertFalse(aggregatedFeeds.isEmpty());
        for (AggregatedFeed af:aggregatedFeeds) {
            aggregatedFeedService.delete(af);
        }
        log.log(Level.INFO, "{0} aggregatedfeeds after deleting", aggregatedFeedService.getNumAggregatedFeeds());
        assertTrue(aggregatedFeedService.getNumAggregatedFeeds() < old);
    }
}