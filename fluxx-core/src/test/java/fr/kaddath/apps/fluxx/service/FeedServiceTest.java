package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.Category;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;

public class FeedServiceTest extends AbstractTest {

	private Feed createCompleteFeed() {
		Feed feed = new Feed();
		feed.setUrl(VALID_URL);
		feed.setTitle(VALID_TITLE);

		Item item = new Item(VALID_LINK, feed);

		Set<DownloadableItem> downloadableItems = new HashSet<DownloadableItem>();
		DownloadableItem downloadableItem = new DownloadableItem(item);
		downloadableItem.setFileLength(FILE_LENGTH);
		downloadableItem.setType(TYPE);
		downloadableItem.setUrl(VALID_LINK);
		downloadableItems.add(downloadableItem);
		item.setDownloadableItems(downloadableItems);

		Set<Category> categories = new HashSet<Category>();
		Category feedCategory = new Category();
		feedCategory.setName(CATEGORY_NAME);
		categories.add(feedCategory);

		item.setCategories(categories);

		feed.getItems().add(item);
		return feed;
	}

	@Test
	public void should_persist_a_feed() {
		Feed feed = createCompleteFeed();
		feedService.persist(feed);
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
	public void should_return_feeds_in_error() throws Exception {
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
	public void should_return_all_feeds() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		Feed feed2 = createFeedWithCategories();
		List<Feed> feeds = feedService.findAllFeeds();
		assertTrue(feeds.contains(feed1));
		assertTrue(feeds.contains(feed2));
	}

	@Test
	public void testFindAllFeedsNotInError() throws Exception {
		createFeedWithDownloadableItems();
		List<Feed> feeds = feedService.findAllFeedsNotInError();
		assertFalse(feeds.isEmpty());
		for (Feed feed : feeds) {
			assertFalse(feed.getInError());
		}
	}

	@Test
	public void testFindAllFeedsInError() throws Exception {
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
	public void should_not_return_feed_in_custom_feed() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		createFeedWithCategories();
		CustomFeed aggregatedFeed = createCustomFeedWithOneFeed(feed1);
		List<Feed> feeds = feedService.findAvailableFeedsByAggregatedFeed(aggregatedFeed);
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
		long initialSize = feedService.getNumFeeds();
		createFeedWithDownloadableItems();
		createFeedWithCategories();
		assertEquals(initialSize + 2, feedService.getNumFeeds().longValue());
	}

	@Test
	public void should_find_all_categories_of_a_feed() throws Exception {
		Feed feed = createFeedWithCategories();
		List<String> categories = feedService.findCategoriesByFeed(feed);
		assertFalse(categories.isEmpty());
	}

	@Test
	public void testFindFeedById() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		Feed feed2 = feedService.findFeedById(feed1.getId());
		assertEquals(feed1, feed2);
	}

	@Test
	public void testFindFeedByUrl() throws Exception {
		Feed feed1 = createFeedWithDownloadableItems();
		Feed feed2 = feedService.findFeedByUrl(feed1.getUrl());
		assertEquals(feed1, feed2);
	}

	@Test
	public void testGetNumFeedType() throws Exception {
		createFeedWithDownloadableItems();
		Map<String, Long> feedType = feedService.getNumFeedType();
		assertFalse(feedType.isEmpty());

	}

}