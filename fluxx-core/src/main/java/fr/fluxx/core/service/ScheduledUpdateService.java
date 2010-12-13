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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.sun.syndication.feed.synd.SyndFeed;

import fr.fluxx.core.FeedUpdateData;
import fr.fluxx.core.cache.CustomFeedCache;
import fr.fluxx.core.collection.Pair;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.exception.DownloadFeedException;

@Singleton
public class ScheduledUpdateService {

	private static final Logger LOG = Logger.getLogger(ScheduledUpdateService.class.getName());

	@EJB
	private FeedService feedService;

	@EJB
	private AsynchronousFeedDownloaderService downloaderService;

	@EJB
	private CustomFeedCache feedCache;

	@EJB
	private FeedFetcherService feedFetcherService;

	private static final Map<Feed, Integer> FEED_SIZES = new HashMap<Feed, Integer>();

	public void updateTopPriorityFeeds() {
		LOG.info("Top priority update is starting ...");
		List<Feed> feeds = feedService.findAllTopPriorityFeeds();
		update(feeds);
		LOG.info("Top priority update is stopping ...");
	}

	@Schedule(minute = "*/15", hour = "*")
	public void updateAll() {
		LOG.info("Full update is starting ...");
		List<Feed> feeds = feedService.findFeedsToUpdate();
		update(feeds);
		LOG.info("Full update is stopping ...");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void update(List<Feed> feeds) {
		FeedUpdateData fud = new FeedUpdateData();
		feedCache.clear();
		for (int i = 0; i < feeds.size(); i++) {
			Feed feed = feeds.get(i);
			int progress = getInPercent(i + 1, feeds.size());
			try {
				LOG.log(Level.INFO, "[{0}%] Update feed [{1}]: {2} ... ",
						new Object[] { progress, feed.getId(), feed.getUrl() });
				Pair<Feed, SyndFeed> couple = downloaderService.downloadFeedContent(feed);
				updateFeed(couple, fud);
			} catch (DownloadFeedException e) {
				feed.setInError(true);
				feed = feedService.store(feed);
				LOG.warning("Download failed for " + feed.getUrl() + " cause : " + e.getMessage());
			}
		}
		LOG.log(Level.INFO, "{0} MB downloaded", fud.getDownloadSizeInMBytes());
	}

	public void updateFeed(Pair<Feed, SyndFeed> couple, FeedUpdateData fud) {
		Feed feed = couple.left();
		SyndFeed syndFeed = couple.right();
		fud.add(feed);
		feedFetcherService.createFromSyndFeed(feed, syndFeed);
		FEED_SIZES.put(feed, feed.getSize());
	}

	private int getInPercent(int counter, float numFeeds) {
		return (int) ((counter / numFeeds) * 100);
	}
}