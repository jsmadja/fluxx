package fr.kaddath.apps.fluxx.service;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

public class FeedFetcherServiceTest extends AbstractTest {

	@Test
	public void should_fetch_url_with_amp() throws DownloadFeedException {
		if (isIntegrationTest) {
			feedFetcherService
					.addNewFeed("http://news.google.fr/news?pz=1&amp;cf=all&amp;ned=fr&amp;hl=fr&amp;topic=w&amp;output=rss");
		}
	}

	@Test
	public void should_fetch_le_monde() throws DownloadFeedException {
		createFeedLeMonde();
	}

	@Test
	public void should_fetch_les_castcodeurs() throws DownloadFeedException {
		if (isIntegrationTest) {
			feedFetcherService.addNewFeed(URL_CASTCODEURS);
		}
	}

	@Test
	public void should_fetch_frandroid() throws DownloadFeedException {
		if (isIntegrationTest) {
			feedFetcherService.addNewFeed(URL_FRANDROID);
		}
	}

	@Test
	public void should_fetch_lnb() throws DownloadFeedException {
		if (isIntegrationTest) {
			feedFetcherService.addNewFeed("http://www.lnb.fr/index_rss.php?id=2&lng=fr");
		}
	}

}