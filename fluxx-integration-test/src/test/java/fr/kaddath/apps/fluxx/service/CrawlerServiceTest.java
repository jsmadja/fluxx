package fr.kaddath.apps.fluxx.service;

import org.junit.Ignore;
import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractIntegrationTest;

@Ignore
public class CrawlerServiceTest extends AbstractIntegrationTest {

	private static final int MAX_FEEDS_TO_ADD = 10;

	@Test
	public void should_crawl() throws Exception {
		crawlerService.crawl("http://www.lemonde.fr", MAX_FEEDS_TO_ADD);
	}

}
