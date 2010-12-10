package fr.kaddath.apps.fluxx.service;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractIntegrationTest;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

public class FeedFetcherServiceTest extends AbstractIntegrationTest {

	private void fetch(String url) throws DownloadFeedException {
		Feed feed = feedService.findFeedByUrl(url);
		if (feed != null) {
			feedService.delete(feed);
		}
		feedFetcherService.addNewFeed(url);
	}

	@Test
	public void should_fetch_url_with_amp() throws DownloadFeedException {
		fetch("http://news.google.fr/news?pz=1&amp;cf=all&amp;ned=fr&amp;hl=fr&amp;topic=w&amp;output=rss");
	}

	@Test
	public void should_fetch_le_monde() throws DownloadFeedException {
		fetch(URL_LE_MONDE);
	}

	@Test
	public void should_fetch_les_castcodeurs() throws DownloadFeedException {
		fetch(URL_CASTCODEURS);
	}

	@Test
	public void should_fetch_frandroid() throws DownloadFeedException {
		fetch(URL_FRANDROID);
	}

	@Test
	public void should_fetch_lnb() throws DownloadFeedException {
		fetch("http://www.lnb.fr/index_rss.php?id=2&lng=fr");
	}

	@Test
	public void should_fetch_lespasperdus() throws DownloadFeedException {
		fetch("http://lespasperdus.blogspot.com/feeds/posts/default?alt=rss");
	}

}