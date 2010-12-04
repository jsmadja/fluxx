package fr.kaddath.apps.fluxx.service;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import fr.kaddath.apps.fluxx.cache.CustomFeedCache;
import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.exception.InvalidItemException;

@Stateless
public class FeedFetcherService {

	private static final Logger LOG = Logger.getLogger(FeedFetcherService.class.getName());

	@EJB
	AsynchronousFeedDownloaderService downloaderService;

	@EJB
	ItemService itemService;

	@EJB
	FeedService feedService;

	@EJB
	ItemBuilderService itemBuilderService;

	@PersistenceContext
	EntityManager em;

	@EJB
	CustomFeedCache feedCache;

	private static final int ONE_MEGABYTE = 1048576;

	private static final Map<Feed, Integer> feedSizes = new HashMap<Feed, Integer>();

	private static int analyzeSize = 0;

	@Schedule(minute = "*/15", hour = "*", persistent = true)
	public void updateTopPriorityFeeds() {
		LOG.info("Top priority update is starting ...");
		analyzeSize = 0;
		feedCache.clear();
		List<Pair<Feed, SyndFeed>> couples = downloadTopPriorityFeeds();
		int downloadSize = computeDownloadSize(couples);
		updateFeeds(couples);
		logUpdateStats(downloadSize);
	}

	private void logUpdateStats(int downloadSize) {
		LOG.info("Update is finished (" + convertInMegaBytes(downloadSize) + " MB downloaded, "
				+ convertInMegaBytes(analyzeSize) + " MB analyzed)");
	}

	@Schedule(minute = "0", hour = "*/1", persistent = true)
	public void updateAll() {
		LOG.info("Full update is starting ...");
		analyzeSize = 0;
		feedCache.clear();
		List<Pair<Feed, SyndFeed>> couples = downloadAllFeeds();
		int downloadSize = computeDownloadSize(couples);
		updateFeeds(couples);
		logUpdateStats(downloadSize);
	}

	private int computeDownloadSize(List<Pair<Feed, SyndFeed>> couples) {
		int size = 0;
		for (Pair<Feed, SyndFeed> couple : couples) {
			Feed feed = couple.left();
			size += feed.getSize();
		}
		return size;
	}

	private String convertInMegaBytes(int size) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		return df.format((double) size / ONE_MEGABYTE);
	}

	private List<Pair<Feed, SyndFeed>> downloadAllFeeds() {
		List<Feed> feeds = feedService.findAllFeedsNotInError();
		return downloadFeeds(feeds);
	}

	private List<Pair<Feed, SyndFeed>> downloadTopPriorityFeeds() {
		List<Feed> feeds = feedService.findAllTopPriorityFeeds();
		return downloadFeeds(feeds);
	}

	private List<Pair<Feed, SyndFeed>> downloadFeeds(List<Feed> feeds) {
		List<Pair<Feed, SyndFeed>> couples = new ArrayList<Pair<Feed, SyndFeed>>();

		for (int i = 0; i < feeds.size(); i++) {
			Feed feed = feeds.get(i);
			try {
				Pair<Feed, SyndFeed> couple = downloaderService.downloadFeedContent(feed);
				couples.add(couple);
			} catch (DownloadFeedException e) {
				feed.setInError(true);
				feed = store(feed);
				LOG.warning("Download failed for " + feed.getUrl() + " cause : " + e.getMessage());
			}
		}
		return couples;
	}

	private void updateFeeds(List<Pair<Feed, SyndFeed>> couples) {
		for (int i = 0; i < couples.size();) {
			Pair<Feed, SyndFeed> couple = couples.get(i);
			Feed feed = couple.left();
			SyndFeed syndFeed = couple.right();
			if (mustBeUpdated(feed)) {
				analyzeSize += feed.getSize();
				createFromSyndFeed(feed, syndFeed);
				LOG.log(Level.INFO, "[{0}%] Update feed : {1} ... updated successfully",
						new Object[] { getInPercent(i + 1, couples.size()), feed.getTitle() });
			} else {
				// LOG.log(Level.INFO, "[{0}%] Skip feed : {1}",
				// new Object[] { getInPercent(i + 1, couples.size()), feed.getTitle() });
			}
			feedSizes.put(feed, feed.getSize());
			i++;
		}
	}

	public Feed addNewFeed(String feedUrl) throws DownloadFeedException {
		feedUrl = clean(feedUrl);

		Feed feed = feedService.findFeedByUrl(feedUrl);
		if (feed == null) {
			feed = fetch(new Feed(feedUrl));
		}
		return feed;
	}

	private String clean(String url) {
		try {
			url = StringUtils.trimToEmpty(url);
			url = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(url);
			url = URLDecoder.decode(url, "UTF-8");
			url = url.trim();
		} catch (UnsupportedEncodingException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}

		return url;
	}

	private Feed fetch(Feed feed) throws DownloadFeedException {
		try {
			URL feedUrl = new URL(feed.getUrl());
			SyndFeedInput syndFeedInput = new SyndFeedInput();
			SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));
			updateWithFeedContent(feed, syndFeed);
			return store(feed);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			throw new DownloadFeedException(e.getMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateExistingFeed(Feed feed) {
		try {
			feed.setInError(false);
			fetch(feed);
		} catch (Throwable e) {
			LOG.warning(e.getMessage());
			feed.setInError(true);
			feed = store(feed);
		}
	}

	public Feed createFromSyndFeed(Feed feed, SyndFeed syndFeed) {
		updateFeedInformations(feed, syndFeed);
		updateFeedItems(feed, syndFeed);
		return store(feed);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void updateWithFeedContent(Feed feed, SyndFeed syndFeed) {
		try {
			feed.setInError(false);
			createFromSyndFeed(feed, syndFeed);
		} catch (Exception e) {
			LOG.warning(e.getMessage());
			feed.setInError(true);
			feed = store(feed);
		}
	}

	private int getInPercent(int counter, float numFeeds) {
		return (int) ((counter / numFeeds) * 100);
	}

	private void updateFeedInformations(Feed feed, SyndFeed syndFeed) {
		feed.setAuthor(syndFeed.getAuthor());
		if (StringUtils.isEmpty(feed.getAuthor()) && syndFeed.getAuthors() != null && !syndFeed.getAuthors().isEmpty()) {
			SyndPerson person = (SyndPerson) syndFeed.getAuthors().get(0);
			feed.setAuthor(person.getName());
		}
		if (StringUtils.isBlank(feed.getAuthor())) {
			feed.setAuthor(syndFeed.getCopyright());
		}
		feed.setDescription(syndFeed.getDescription());
		feed.setEncoding(syndFeed.getEncoding());
		feed.setFeedType(syndFeed.getFeedType());
		feed.setPublishedDate(syndFeed.getPublishedDate());
		feed.setTitle(syndFeed.getTitle());
	}

	private void updateFeedItems(Feed feed, SyndFeed syndFeed) {
		for (Object o : syndFeed.getEntries()) {
			SyndEntryImpl syndEntryImpl = (SyndEntryImpl) o;
			String link = syndEntryImpl.getLink();
			if (link != null) {
				Item item = itemService.findItemByLink(link);
				if (item == null) {
					try {
						item = itemBuilderService.createItemFromSyndEntry(syndEntryImpl);
						item.setFeed(feed);
						feed.getItems().add(item);
						feed.setLastUpdate(Calendar.getInstance().getTime());
					} catch (InvalidItemException e) {
						LOG.info(e.getMessage());
					} catch (Exception e) {
						LOG.severe(e.getMessage());
					}
				}
			}
		}
	}

	private boolean mustBeUpdated(Feed feed) {
		Integer oldSize = feedSizes.get(feed);
		return oldSize == null || feed.getSize() != oldSize;
	}

	private Feed store(Feed feed) {
		if (feed.getId() == null) {
			em.persist(feed);
		} else {
			feed = em.merge(feed);
		}
		em.flush();
		return feed;
	}

	public boolean exists(String feedUrl) {
		return feedService.findFeedByUrl(feedUrl) != null;
	}
}
