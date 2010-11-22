package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

public class RssServiceTest extends AbstractTest {

	@Test
	public void withPodcast() throws Exception {
		AggregatedFeed aggregatedFeed = createAggregatedFeedWithFeed(FEED_CASTCODEURS);
		String rss = rssService.getRssFeedById(aggregatedFeed.getId(), request, "UTF-8");
		assertTrue(rss.contains("enclosure"));
	}

	@Test
	public void withCategory() throws DownloadFeedException {
		AggregatedFeed feed = createAggregatedFeedWithFeed(FEED_FRANDROID);
		String rss = rssService.getRssFeedById(feed.getId(), request, "UTF-8");
		assertTrue(rss.contains("category"));
	}
}