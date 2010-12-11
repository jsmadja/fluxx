package fr.kaddath.apps.fluxx.service;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import fr.kaddath.apps.fluxx.crawler.FeedCrawler;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless
@Interceptors({ChronoInterceptor.class})
public class CrawlerService {

    private static final int NUMBER_OF_CRAWLERS = 1;

    public void crawl(String url, int maxFeedsToAdd) throws Exception {
        CrawlController controller = new CrawlController("/tmp/fluxx/crawl");
        controller.addSeed(url);
        FeedCrawler.MAX_FEEDS_TO_ADD = maxFeedsToAdd;
        controller.start(FeedCrawler.class, NUMBER_OF_CRAWLERS);
    }

}
