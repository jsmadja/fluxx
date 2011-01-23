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

import fr.fluxx.core.AbstractTest;
import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.domain.Item;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        String expResult = "http://" + serverName + ":" + SERVER_PORT + contextPath + "/rss/" + feed.getUsername()+"/"+feed.getCategory();
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