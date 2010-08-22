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
        createFeed();
        Long numItems = itemService.getNumItems();
        assertTrue(numItems > 0);
    }

    @Test
    public void testFindLastItem() throws Exception {
        Feed feed = createFeed();
        Item item = itemService.findLastItem(feed);
        List<Item> items = itemService.findItemsByFeed(feed);
        assertFalse(items.isEmpty());
        isLast(items, item);
    }

    @Test
    public void testFindFirstItem() throws Exception {
        Feed feed = createFeed();
        Item item = itemService.findFirstItem(feed);
        List<Item> items = itemService.findItemsByFeed(feed);
        assertFalse(items.isEmpty());
        isFirst(items, item);
    }

    @Test
    public void testGetNumItemsByDay() throws Exception {
        Feed feed = createFeed();
        List<Item> items = itemService.findItemsByFeed(feed);
        for (Item item:items) {
            Date date = item.getPublishedDate();
            Long numItems = itemService.getNumItemsByDay(date);
            assertTrue(numItems>0);
        }
    }

    @Test
    public void testGetFirstItem() throws Exception {
        Feed feed = createFeed();
        Item item = itemService.getFirstItem();
        assertNotNull(item);
        List<Item> items = itemService.findItemsByFeed(feed);
        assertFalse(items.isEmpty());
        isFirst(items, item);
    }

    @Test
    public void testGetLastItem() throws Exception {
        Feed feed = createFeed();
        Item item = itemService.getLastItem();
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