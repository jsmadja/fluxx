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

package fr.kaddath.apps.fluxx.service;

import org.junit.Assert;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

import fr.kaddath.apps.fluxx.AbstractIntegrationTest;
import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

public class AsynchronousFeedDownloaderServiceTest extends AbstractIntegrationTest {

	@Test
	public void should_download_feed_successfully() throws DownloadFeedException {
		Feed feed = new Feed();
		feed.setUrl(AbstractIntegrationTest.URL_CASTCODEURS);
		Pair<Feed, SyndFeed> objects = asynchronousFeedDownloaderService.downloadFeedContent(feed);
		SyndFeed s = objects.right();
		feed = objects.left();
		Assert.assertTrue(feed.getSize() > 0);
		Assert.assertNotNull(s.getLink());
	}
}
