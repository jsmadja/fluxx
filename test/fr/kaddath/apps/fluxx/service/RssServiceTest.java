package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import org.junit.Test;
import static org.junit.Assert.*;

public class RssServiceTest extends AbstractTest {

    @Test
    public void withPodcast() throws Exception {
        String url = "http://lescastcodeurs.libsyn.com/rss";
        AggregatedFeed aggregatedFeed = createAggregatedFeedWithFeed(url);
        String rss = rssService.getRssFeedById(aggregatedFeed.getAggregatedFeedId(), request);
        assertTrue(rss.contains("enclosure"));
    }

    @Test
    public void withCategory() throws DownloadFeedException {
        String url = "http://feeds2.feedburner.com/Frandroid";
        AggregatedFeed feed = createAggregatedFeedWithFeed(url);
        String rss = rssService.getRssFeedById(feed.getAggregatedFeedId(), request);
        assertTrue(rss.contains("category"));
    }
}