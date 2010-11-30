package fr.kaddath.apps.fluxx.service;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;

public class CrawlerServiceTest extends AbstractTest {

	private static final int MAX_FEEDS_TO_ADD = 3;

	@Test
	public void should_crawl() throws Exception {
		if (isIntegrationTest) {
			crawlerService.crawl(VALID_LINK, MAX_FEEDS_TO_ADD);
		}
	}

}
