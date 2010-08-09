package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.AbstractTest;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemServiceTest extends AbstractTest {

    @Test
    public void testFindItemsByLink() throws Exception {
        Feed feed = createFeed();
        Item item1 = itemService.findItemsByFeed(feed).get(0);
        Item item2 = itemService.findItemByLink(item1.getLink());
        assertEquals(item1, item2);
    }

    @Test
    public void testFindItemsByFeedAndAfter() throws Exception {
        Feed feed = createFeed();
        Item item = itemService.findFirstItem(feed);
        List<Item> items = itemService.findItemsByFeedAndAfter(feed, item.getPublishedDate());
        assertEquals(itemService.getNumItems(feed), items.size());
    }

    @Test
    public void testFindItemsByFeed() throws Exception {
        Feed feed = createFeed();
        List<Item> items = itemService.findItemsByFeed(feed);
        assertEquals(itemService.getNumItems(feed), items.size());
    }

    @Test
    public void testGetNumItems_0args() throws Exception {
        Feed feed = createFeed();
        Long numItems = itemService.getNumItems(feed);
        assertEquals(itemService.getNumItems(feed), numItems.longValue());
    }

    @Test
    public void testGetNumItems_Feed() throws Exception {
        Feed feed = createFeed();
        Long numItems = itemService.getNumItems();
        assertTrue(numItems > 0);
    }

    @Test
    public void testFindLastItem() throws Exception {
        Feed feed = createFeed();
        Item item = itemService.findLastItem(feed);
        List<Item> items = itemService.findItemsByFeed(feed);
        assertFalse(items.isEmpty());
        for (Item i:items) {
            if (!i.equals(item)) {
                assertTrue(item.getPublishedDate().after(i.getPublishedDate()));
            }
        }
    }

    @Test
    public void testFindFirstItem() throws Exception {
        Feed feed = createFeed();
        Item item = itemService.findFirstItem(feed);
        List<Item> items = itemService.findItemsByFeed(feed);
        assertFalse(items.isEmpty());
        for (Item i:items) {
            if (!i.equals(item)) {
                assertTrue(item.getPublishedDate().before(i.getPublishedDate()));
            }
        }
    }
}