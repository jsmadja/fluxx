package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import org.junit.Test;
import static org.junit.Assert.*;

public class RssServiceTest extends AbstractTest {
    
    @Test
    public void withPodcast() throws Exception {
        AggregatedFeed aggregatedFeed = createAggregatedFeedWithFeed(FEED_CASTCODEURS);
        String rss = rssService.getRssFeedById(aggregatedFeed.getAggregatedFeedId(), request);
        assertTrue(rss.contains("enclosure"));
    }

    @Test
    public void withCategory() throws DownloadFeedException {
        AggregatedFeed feed = createAggregatedFeedWithFeed(FEED_FRANDROID);
        String rss = rssService.getRssFeedById(feed.getAggregatedFeedId(), request);
        assertTrue(rss.contains("category"));
    }
}