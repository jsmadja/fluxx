package fr.kaddath.apps.fluxx.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import com.sun.syndication.feed.synd.SyndFeed;

import fr.kaddath.apps.fluxx.FeedUpdateData;
import fr.kaddath.apps.fluxx.cache.CustomFeedCache;
import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@Interceptors({ ChronoInterceptor.class })
public class ScheduledUpdateService {

	private static final Logger LOG = Logger.getLogger(ScheduledUpdateService.class.getName());

	@EJB
	FeedService feedService;

	@EJB
	AsynchronousFeedDownloaderService downloaderService;

	@EJB
	CustomFeedCache feedCache;

	@EJB
	FeedFetcherService feedFetcherService;

	private static final Map<Feed, Integer> FEED_SIZES = new HashMap<Feed, Integer>();

	// @Schedule(minute = "*/15", hour = "*")
	public void updateTopPriorityFeeds() {
		LOG.info("Top priority update is starting ...");
		List<Feed> feeds = feedService.findAllTopPriorityFeeds();
		update(feeds);
		LOG.info("Top priority update is stopping ...");
	}

	// @Schedule(minute = "0", hour = "0,12")
	public void updateAll() {
		LOG.info("Full update is starting ...");
		List<Feed> feeds = feedService.findAllFeedsNotInError();
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
		fud.log();
	}

	public void updateFeed(Pair<Feed, SyndFeed> couple, FeedUpdateData fud) {
		Feed feed = couple.left();
		SyndFeed syndFeed = couple.right();
		fud.addDownload(feed);
		if (mustBeUpdated(feed)) {
			fud.addAnalyze(feed);
			feedFetcherService.createFromSyndFeed(feed, syndFeed);
		}
		FEED_SIZES.put(feed, feed.getSize());
	}

	private boolean mustBeUpdated(Feed feed) {
		Integer oldSize = FEED_SIZES.get(feed);
		return oldSize == null || !oldSize.equals(feed.getSize());
	}

	private int getInPercent(int counter, float numFeeds) {
		return (int) ((counter / numFeeds) * 100);
	}
}
