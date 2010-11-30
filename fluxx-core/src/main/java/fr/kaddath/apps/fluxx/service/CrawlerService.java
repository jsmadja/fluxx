package fr.kaddath.apps.fluxx.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import fr.kaddath.apps.fluxx.crawler.FeedCrawler;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@Interceptors({ ChronoInterceptor.class })
public class CrawlerService {

	@EJB
	FeedFetcherService feedFetcherService;

	private static final int NUMBER_OF_CRAWLERS = 20;

	public void crawl(String url, int maxFeedsToAdd) throws Exception {
		CrawlController controller = new CrawlController("/tmp/crawl/root");
		controller.addSeed(url);
		FeedCrawler.MAX_FEEDS_TO_ADD = maxFeedsToAdd;
		controller.start(FeedCrawler.class, NUMBER_OF_CRAWLERS);
	}

}
