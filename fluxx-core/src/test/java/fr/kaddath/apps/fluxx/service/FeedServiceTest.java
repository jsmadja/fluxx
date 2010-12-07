package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.Category;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

public class FeedServiceTest extends AbstractTest {

	@Test
	public void should_persist_a_feed() {
		Feed feed = createCompleteFeed();
		feed = feedService.store(feed);
		Feed actualFeed = feedService.findFeedById(feed.getId());
		assertEquals(feed, actualFeed);
	}

	@Test
	public void should_delete_a_feed() throws Exception {
		Feed feed = createFeedWithCategories();
		long id = feed.getId();
		assertNotNull(feedService.findFeedById(id));
		feedService.delete(feed);
		assertNull(feedService.findFeedById(id));
	}

	@Test
	public void should_find_feeds_in_error() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		feed.setInError(true);
		feedService.update(feed);
		List<Feed> feeds = feedService.findFeedsByInError(true, 0);
		assertFalse(feeds.isEmpty());
		for (Feed f : feeds) {
			assertTrue(f.getInError());
		}
	}

	@Test
	public void should_find_all_feeds_not_in_error() throws Exception {
		createFeedWithDownloadableItems();
		List<Feed> feeds = feedService.findAllFeedsNotInError();
		assertFalse(feeds.isEmpty());
		for (Feed feed : feeds) {
			assertFalse(feed.getInError());
		}
	}

	@Test
	public void should_find_all_feeds_in_error() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		feed.setInError(true);
		feedService.update(feed);
		List<Feed> feeds = feedService.findAllFeedsInError();
		assertFalse(feeds.isEmpty());
		for (Feed f : feeds) {
			assertTrue(f.getInError());
		}
	}

	@Test
	public void should_not_find_feed_in_available_feeds_when_feed_is_already_in() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		createFeedWithCategories();
		CustomFeed customFeed = createCustomFeedWithOneFeed(feed1);
		List<Feed> feeds = feedService.findAvailableFeedsByCustomFeed(customFeed);
		assertFalse(feeds.contains(feed1));
	}

	@Test
	public void should_return_one_available_feed_which_match_the_filter() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		Feed feed2 = createFeedWithCategories();
		CustomFeed customFeed = createCustomFeedWithOneFeed(feed1);
		List<Feed> feeds = feedService.findAvailableFeedsByCustomFeedWithFilter(customFeed, feed2.getTitle());
		assertEquals(1, feeds.size());
	}

	@Test
	public void should_find_feeds_when_searching_by_item_title() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		Item item = itemService.findItemsByFeed(feed).get(0);
		List<Feed> feeds = feedService.findFeedsByItemTitle(item.getTitle());
		assertFalse(feeds.isEmpty());
	}

	@Test
	public void should_find_feeds_when_searching_by_category_name() throws Exception {
		Feed feed = createFeedWithCategories();
		Item item = itemService.findItemsByFeed(feed).get(0);
		Category category = item.getCategories().iterator().next();
		List<Feed> feeds = feedService.findFeedsByCategory(category.getName());
		assertFalse(feeds.isEmpty());
	}

	@Test
	public void should_find_feeds_when_searching_by_author() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		List<Feed> feeds = feedService.findFeedsByDescriptionUrlAuthorTitle(feed.getAuthor());
		assertFalse(feeds.isEmpty());
	}

	@Test
	public void should_return_two_more_feeds_after_adding_two_feeds() throws Exception {
		feedService.getNumFeeds();
		createFeedWithDownloadableItems();
		createFeedWithCategories();
		assertTrue(feedService.getNumFeeds() > 0);
	}

	@Test
	public void should_find_all_categories_of_a_feed() throws Exception {
		Feed feed = createFeedWithCategories();
		List<String> categories = feedService.findCategoriesByFeed(feed);
		assertFalse(categories.isEmpty());
	}

	@Test
	public void should_return_the_same_feed_when_searching_by_id() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		Feed feed2 = feedService.findFeedById(feed1.getId());
		assertEquals(feed1, feed2);
	}

	@Test
	public void should_return_the_same_feed_when_searching_by_url() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		Feed feed2 = feedService.findFeedByUrl(feed1.getUrl());
		assertEquals(feed1, feed2);
	}

	@Test
	public void should_return_feed_types() throws Exception {
		createFeedWithDownloadableItems();
		Map<String, Long> feedType = feedService.getNumFeedType();
		assertFalse(feedType.isEmpty());
	}

	@Test
	public void should_find_last_updated_feed() {
		createFeedWithCategories();
		Feed feed = feedService.findLastUpdatedFeed();
		assertNotNull(feed);
	}

	@Test
	public void should_find_all_top_priority_feeds() throws DownloadFeedException {
		Feed f1 = createFeedWithCategories();
		createCustomFeedWithOneFeed(f1);
		List<Feed> feeds = feedService.findAllTopPriorityFeeds();
		assertFalse(feeds.isEmpty());
	}
}