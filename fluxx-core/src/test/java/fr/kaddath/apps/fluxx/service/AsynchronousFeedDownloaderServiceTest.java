package fr.kaddath.apps.fluxx.service;

import org.junit.Assert;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

public class AsynchronousFeedDownloaderServiceTest {

	private final AsynchronousFeedDownloaderService service = new AsynchronousFeedDownloaderService();

	@Test
	public void should_download_feed_successfully() throws DownloadFeedException {
		Feed feed = new Feed();
		feed.setUrl(AbstractTest.FEED_CASTCODEURS);
		Object[] objects = service.downloadFeedContent(feed);
		SyndFeed s = (SyndFeed) objects[1];
		feed = (Feed) objects[0];
		Assert.assertTrue(feed.getSize() > 0);
		Assert.assertNotNull(s.getLink());
	}
}
