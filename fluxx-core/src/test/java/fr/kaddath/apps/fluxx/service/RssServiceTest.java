package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RssServiceTest extends AbstractTest {

    @Test
    public void withPodcast() throws Exception {
        CustomFeed aggregatedFeed = createCustomFeedWithOneFeed(createFeedWithDownloadableItems());
        String rss = rssService.getRssFeedById(aggregatedFeed.getId(), request, "UTF-8");
        assertTrue(rss.contains("enclosure"));
    }

    @Test
    public void withCategory() throws DownloadFeedException {
        CustomFeed feed = createCustomFeedWithOneFeed(createFeedWithDownloadableItems());
        String rss = rssService.getRssFeedById(feed.getId(), request, "UTF-8");
        assertTrue(rss.contains("category"));
    }
}