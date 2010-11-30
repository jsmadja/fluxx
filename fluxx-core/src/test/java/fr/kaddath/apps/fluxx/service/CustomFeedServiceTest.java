package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Item;

public class CustomFeedServiceTest extends AbstractTest {

	@Test
	public void should_add_a_new_custom_feed() {
		Long initialCount = customFeedService.getNumCustomFeeds();
		customFeedService.addCustomFeed("fluxxer", createRandomString(), 7);
		assertEquals(initialCount + 1, customFeedService.getNumCustomFeeds().longValue());
	}

	@Test
	public void should_create_a_valid_url() throws Exception {
		CustomFeed feed = createCustomFeed();
		String expResult = "http://" + serverName + ":" + serverPort + contextPath + "/rss?id=" + feed.getId();
		String result = customFeedService.createUrl(request, feed);
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
	public void should_return_an_existing_custom_feed_when_searching_by_its_id() {
		CustomFeed customFeed = customFeedService.addCustomFeed("fluxxer", createRandomString(), 7);
		CustomFeed af = customFeedService.findById(customFeed.getId());
		assertEquals(customFeed.getId(), af.getId());
	}

	@Test
	public void should_delete_two_feeds() {
		CustomFeed af1 = createCustomFeed();
		CustomFeed af2 = createCustomFeed();
		Long initialCount = customFeedService.getNumCustomFeeds();
		customFeedService.delete(af1);
		customFeedService.delete(af2);
		assertEquals(initialCount - 2, customFeedService.getNumCustomFeeds().longValue());
	}
}