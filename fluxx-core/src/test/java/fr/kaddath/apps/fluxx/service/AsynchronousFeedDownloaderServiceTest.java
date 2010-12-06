package fr.kaddath.apps.fluxx.service;

import org.junit.Assert;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

public class AsynchronousFeedDownloaderServiceTest extends AbstractTest {

	@Test
	public void should_download_feed_successfully() throws DownloadFeedException {
		if (isIntegrationTest) {
			Feed feed = new Feed();
			feed.setUrl(AbstractTest.URL_CASTCODEURS);
			Pair<Feed, SyndFeed> objects = asynchronousFeedDownloaderService.downloadFeedContent(feed);
			SyndFeed s = objects.right();
			feed = objects.left();
			Assert.assertTrue(feed.getSize() > 0);
			Assert.assertNotNull(s.getLink());
		}
	}
}
