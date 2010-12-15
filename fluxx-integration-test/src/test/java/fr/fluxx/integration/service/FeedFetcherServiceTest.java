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

package fr.fluxx.integration.service;

import org.junit.Test;

import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.exception.DownloadFeedException;
import fr.fluxx.integration.AbstractIntegrationTest;

public class FeedFetcherServiceTest extends AbstractIntegrationTest {

	private Feed fetch(String url) throws DownloadFeedException {
		Feed feed = feedService.findFeedByUrl(url);
		if (feed != null) {
			feedService.delete(feed);
		}
		return feedFetcherService.addNewFeed(url);
	}

	@Test
	public void should_fetch_url_with_amp() throws DownloadFeedException {
		fetch("http://news.google.fr/news?pz=1&amp;cf=all&amp;ned=fr&amp;hl=fr&amp;topic=w&amp;output=rss");
	}

	@Test
	public void should_fetch_le_monde() throws DownloadFeedException {
		fetch(URL_LE_MONDE);
	}

	@Test
	public void should_fetch_les_castcodeurs() throws DownloadFeedException {
		fetch(URL_CASTCODEURS);
	}

	@Test
	public void should_fetch_frandroid() throws DownloadFeedException {
		fetch(URL_FRANDROID);
	}

	@Test
	public void should_fetch_lnb() throws DownloadFeedException {
		fetch("http://www.lnb.fr/index_rss.php?id=2&lng=fr");
	}

	@Test
	public void should_fetch_lespasperdus() throws DownloadFeedException {
		fetch("http://lespasperdus.blogspot.com/feeds/posts/default?alt=rss");
	}

	@Test
	public void should_fetch_broadcast_rackspace() throws DownloadFeedException {
		fetch("http://broadcast.rackspace.com/ror.xml");
	}

}