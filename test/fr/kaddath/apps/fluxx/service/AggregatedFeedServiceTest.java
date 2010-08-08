package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class AggregatedFeedServiceTest extends AbstractTest {

    @Test
    public void addAggregatedFeed() {
        Long old = aggregatedFeedService.getNumAggregatedFeeds();
        Fluxxer user = giveMeOneFluxxer();
        aggregatedFeedService.addAggregatedFeed(user, "name", 7);
        assertTrue(aggregatedFeedService.getNumAggregatedFeeds() > old);
    }

    @Test
    public void testCreateUrl() throws Exception {
        AggregatedFeed feed = giveMeOneFluxxer().getAggregatedFeeds().get(0);
        String expResult = "http://"+serverName+":"+serverPort+contextPath+"/rss?id="+feed.getAggregatedFeedId();
        String result = aggregatedFeedService.createUrl(request, feed);
        assertEquals(expResult, result);
    }

    @Test
    public void delete() {
        Fluxxer user;
        List<AggregatedFeed> aggregatedFeeds = giveMeOneFluxxer().getAggregatedFeeds();
        for (AggregatedFeed af:aggregatedFeeds) {
            aggregatedFeedService.delete(af);
        }
    }

    @Test
    public void findItemsByAggregatedFeed() {
        List<AggregatedFeed> aggregatedFeeds = giveMeOneFluxxer().getAggregatedFeeds();
        for (AggregatedFeed af:aggregatedFeeds) {
            assertTrue(aggregatedFeedService.findItemsByAggregatedFeed(af).size()>=0);
        }
    }

    @Test
    public void findByAggregatedFeedId() {
        List<AggregatedFeed> aggregatedFeeds = giveMeOneFluxxer().getAggregatedFeeds();
        for (AggregatedFeed af:aggregatedFeeds) {
            assertNotNull(aggregatedFeedService.findByAggregatedFeedId(af.getAggregatedFeedId()));
        }
    }
}