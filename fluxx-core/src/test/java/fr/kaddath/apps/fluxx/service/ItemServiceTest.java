package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;

public class ItemServiceTest extends AbstractTest {

	@Test
	public void should_find_an_existing_item_by_link() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		Item item1 = itemService.findItemsByFeed(feed).get(0);
		Item item2 = itemService.findItemByLink(item1.getLink());
		assertEquals(item1, item2);
	}

	@Test
	public void should_find_all_items_of_a_feed_when_search_begin_at_first_item_date() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		Item item = itemService.findFirstItemOfFeed(feed);
		List<Item> items = itemService.findItemsOfFeedAndAfterDate(feed, item.getPublishedDate());
		List<Item> expectedItems = itemService.findItemsByFeed(feed);
		assertEquals(expectedItems.size(), items.size());
	}

	@Test
	public void should_find_items_when_searching_items_from_existing_feed() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		List<Item> items = itemService.findItemsByFeed(feed);
		assertFalse(items.isEmpty());
	}

	@Test
	public void should_return_the_exact_item_size_of_an_existing_feed() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		int numItems = itemService.findItemsByFeed(feed).size();
		assertEquals(itemService.getNumItemsOfFeed(feed), numItems);
	}

	@Test
	public void should_return_the_number_of_all_items_in_database() throws Exception {
		int size = 0;
		Feed feed1 = createFeedWithDownloadableItems();
		Feed feed2 = createFeedWithCategories();
		size += itemService.findItemsByFeed(feed1).size();
		size += itemService.findItemsByFeed(feed2).size();
		long numItems = itemService.getNumItems();
		assertTrue(numItems >= size);
	}

	@Test
	public void testFindLastItem() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		Item item = itemService.findLastItemOfFeed(feed);
		List<Item> items = itemService.findItemsByFeed(feed);
		assertFalse(items.isEmpty());
		isLast(items, item);
	}

	@Test
	public void should_find_the_first_item() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		Item item = itemService.findFirstItemOfFeed(feed);
		List<Item> items = itemService.findItemsByFeed(feed);
		assertFalse(items.isEmpty());
		isFirst(items, item);
	}

	@Test
	public void testGetNumItemsByDay() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		List<Item> items = itemService.findItemsByFeed(feed);
		for (Item item : items) {
			Date date = item.getPublishedDate();
			Long numItems = itemService.getNumItemsByDay(date);
			assertTrue(numItems > 0);
		}
	}

	@Test
	public void testGetFirstItem() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		Item item = itemService.findFirstItem();
		assertNotNull(item);
		List<Item> items = itemService.findItemsByFeed(feed);
		assertFalse(items.isEmpty());
		isFirst(items, item);
	}

	@Test
	public void testGetLastItem() throws Exception {
		Feed feed = createFeedWithDownloadableItems();
		Item item = itemService.findLastItem();
		assertNotNull(item);
		List<Item> items = itemService.findItemsByFeed(feed);
		assertFalse(items.isEmpty());
		isLast(items, item);
	}

	private void isFirst(List<Item> items, Item item) {
		for (Item i : items) {
			if (!i.equals(item)) {
				assertTrue(item.getPublishedDate().before(i.getPublishedDate()));
			}
		}
	}

	private void isLast(List<Item> items, Item item) {
		for (Item i : items) {
			if (!i.equals(item)) {
				if (item.getPublishedDate() != null) {
					assertTrue(item.getPublishedDate().after(i.getPublishedDate()));
				}
			}
		}
	}

}