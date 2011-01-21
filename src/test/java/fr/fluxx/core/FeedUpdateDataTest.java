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
package fr.fluxx.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.fluxx.core.domain.Feed;

public class FeedUpdateDataTest {

    @Test
    public void should_log_with_0_feed_added() {
        FeedUpdateData fud = new FeedUpdateData();
        assertEquals(0, fud.getDownloadSizeInBytes());
        assertEquals(0, fud.getNumFeeds());
    }

    @Test
    public void should_log_with_1_feed_add() {
        FeedUpdateData fud = new FeedUpdateData();
        Feed feed = new Feed();
        int feedSize = 1;
        feed.setSize(feedSize);
        fud.add(feed);
        assertEquals(1, fud.getNumFeeds());
        assertEquals(feedSize, fud.getDownloadSizeInBytes());
    }

    @Test
    public void should_not_fail_when_passing_null() {
        FeedUpdateData fud = new FeedUpdateData();
        fud.add(null);
        assertEquals(0, fud.getNumFeeds());
        assertEquals(0, fud.getDownloadSizeInBytes());
    }

    @Test
    public void should_not_add_negative_size() {
        FeedUpdateData fud = new FeedUpdateData();
        Feed feed = new Feed();
        feed.setSize(-1);
        fud.add(feed);
        assertEquals(0, fud.getNumFeeds());
        assertEquals(0, fud.getDownloadSizeInBytes());
    }

    @Test
    public void should_convert_in_MBytes() {
        FeedUpdateData fud = new FeedUpdateData();
        Feed feed = new Feed();
        feed.setSize(1024 * 1024);
        fud.add(feed);
        assertEquals("1", fud.getDownloadSizeInMBytes());
    }
}
