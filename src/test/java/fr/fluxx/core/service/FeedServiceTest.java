/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.fluxx.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.fluxx.core.AbstractTest;
import fr.fluxx.core.domain.Category;
import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Item;
import fr.fluxx.core.exception.DownloadFeedException;

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
        feedService.store(feed);
        List<Feed> feeds = feedService.findFeedsByInError(true, 0);
        assertFalse(feeds.isEmpty());
        for (Feed f : feeds) {
            assertTrue(f.getInError());
        }
    }

    @Test
    public void should_not_find_feeds_to_update() throws Exception {
        List<Feed> feeds = feedService.findFeedsToUpdate();
        assertTrue(feeds.isEmpty());
    }

    @Test
    public void should_find_a_feed_to_update() throws Exception {
        createFeedWithDownloadableItems();
        List<Feed> feeds = feedService.findFeedsToUpdate();
        assertTrue(feeds.isEmpty());
    }

    @Test
    public void should_find_all_feeds_in_error() throws Exception {
        Feed feed = createFeedWithDownloadableItems();
        feed.setInError(true);
        feedService.store(feed);
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

    @Test
    public void should_delete_unused_feeds() {
        createFeedWithCategories();
        assertEquals(1, feedService.getNumFeeds().longValue());
        feedService.deleteUnusedFeeds();
        assertEquals(0, feedService.getNumFeeds().longValue());
    }

    @Test
    public void should_not_delete_categories_when_deleting_unused_feeds() {
        createFeedWithCategories();
        Long numCategories = categoryService.getNumCategories();
        feedService.deleteUnusedFeeds();
        assertEquals(numCategories, categoryService.getNumCategories());
    }

    @Test
    public void should_delete_related_items_when_deleting_unused_feeds() {
        createFeedWithCategories();
        assertTrue(itemService.getNumItems() > 0);
        feedService.deleteUnusedFeeds();
        assertEquals(0, itemService.getNumItems().longValue());
    }

    @Test
    public void should_not_delete_used_feeds() {
        Feed f1 = createFeedWithCategories();
        createCustomFeedWithOneFeed(f1);

        Long numItems = itemService.getNumItems();
        Long numCategories = categoryService.getNumCategories();

        assertEquals(1, feedService.getNumFeeds().longValue());
        assertTrue(numItems > 0);
        assertTrue(numCategories > 0);


        feedService.deleteUnusedFeeds();

        assertEquals(1, feedService.getNumFeeds().longValue());
        assertEquals(numItems, itemService.getNumItems());
        assertEquals(numCategories, categoryService.getNumCategories());
    }
}