package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Item;

public class CustomFeedServiceTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(CustomFeedServiceTest.class.getName());

	@Test
	public void addAggregatedFeed() {
		Long old = customFeedService.getNumCustomFeeds();
		log.log(Level.INFO, "{0} aggregatedfeeds before adding", old);
		customFeedService.addCustomFeed("fluxxer", createRandomString(), 7);
		log.log(Level.INFO, "{0} aggregatedfeeds after adding", customFeedService.getNumCustomFeeds());
		assertTrue(customFeedService.getNumCustomFeeds() > old);
	}

	@Test
	public void testCreateUrl() throws Exception {
		CustomFeed feed = createCustomFeed();
		String expResult = "http://" + serverName + ":" + serverPort + contextPath + "/rss?id=" + feed.getId();
		String result = customFeedService.createUrl(request, feed);
		log.info(result);
		assertEquals(expResult, result);
	}

	@Test
	public void should_find_all_items_of_a_feed() {
		CustomFeed customFeed = createCustomFeed();
		customFeedService.addFeed(customFeed, createFeedWithDownloadableItems());
		List<Item> items = customFeedService.findItemsByCustomFeed(customFeed);
		assertFalse(items.isEmpty());
	}

	@Test
	public void findByAggregatedFeedId() {
		CustomFeed aggregatedFeed = customFeedService.addCustomFeed("fluxxer", createRandomString(), 7);
		CustomFeed af = customFeedService.findById(aggregatedFeed.getId());
		assertEquals(aggregatedFeed.getId(), af.getId());
	}

	@Test
	public void delete() {
		CustomFeed af1 = createCustomFeed();
		CustomFeed af2 = createCustomFeed();
		Long old = customFeedService.getNumCustomFeeds();
		log.log(Level.INFO, "{0} aggregatedfeeds before deleting", old);
		customFeedService.delete(af1);
		customFeedService.delete(af2);
		log.log(Level.INFO, "{0} aggregatedfeeds after deleting", customFeedService.getNumCustomFeeds());
		assertTrue(customFeedService.getNumCustomFeeds() < old);
	}
}