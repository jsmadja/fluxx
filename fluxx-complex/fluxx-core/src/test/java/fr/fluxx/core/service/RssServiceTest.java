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
import fr.fluxx.core.exception.DownloadFeedException;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RssServiceTest extends AbstractTest {

    @Test
    public void should_create_a_valid_rss_podcast() throws Exception {
        CustomFeed aggregatedFeed = createCustomFeedWithOneFeed(createFeedWithDownloadableItems());
        String rss = rssService.getRssFeedById(aggregatedFeed.getId(), request, "UTF-8");
        assertTrue(rss.contains("enclosure"));
    }

    @Test
    public void should_create_a_valid_rss() throws DownloadFeedException {
        CustomFeed feed = createCustomFeedWithOneFeed(createFeedWithDownloadableItems());
        String rss = rssService.getRssFeedById(feed.getId(), request, "UTF-8");
        assertTrue(rss.contains("category"));
    }
}