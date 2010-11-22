package fr.kaddath.apps.fluxx.service;

import java.net.URL;
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
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

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
	RssFeedCache feedCache;

	private static final int ONE_MEGABYTE = 1048576;

	private static final Map<Feed, Integer> feedSizes = new HashMap<Feed, Integer>();

	private static int analyzeSize = 0;

	@Interceptors({ ChronoInterceptor.class })
	@Schedule(minute = "*/15", hour = "*", persistent = true)
	public void updateAll() {
		LOG.info("Full update is starting ...");
		analyzeSize = 0;
		feedCache.clear();
		List<Object[]> couples = downloadFeeds();
		int downloadSize = computeDownloadSize(couples);
		updateFeeds(couples);
		LOG.info("Full update is finished (" + convertInMegaBytes(downloadSize) + " MB downloaded, "
				+ convertInMegaBytes(analyzeSize) + " MB analyzed)");
	}

	private int computeDownloadSize(List<Object[]> couples) {
		int size = 0;
		for (Object[] couple : couples) {
			Feed feed = (Feed) couple[0];
			size += feed.getSize();
		}
		return size;
	}

	private String convertInMegaBytes(int size) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		return df.format((double) size / ONE_MEGABYTE);
	}

	@Interceptors({ ChronoInterceptor.class })
	private List<Object[]> downloadFeeds() {
		List<Object[]> couples = new ArrayList<Object[]>();
		List<Feed> feeds = feedService.findAllFeedsNotInError();
		for (int i = 0; i < feeds.size(); i++) {
			Feed feed = feeds.get(i);
			try {
				Object[] couple = downloaderService.downloadFeedContent(feed);
				couples.add(couple);
			} catch (DownloadFeedException e) {
				feed.setInError(true);
				em.merge(feed);
				LOG.warning("Download failed for " + feed.getUrl() + " cause : " + e.getMessage());
			}
		}
		return couples;
	}

	private void updateFeeds(List<Object[]> couples) {
		for (int i = 0; i < couples.size();) {
			Object[] couple = couples.get(i);
			Feed feed = (Feed) couple[0];
			SyndFeed syndFeed = (SyndFeed) couple[1];
			if (mustBeUpdated(feed)) {
				analyzeSize += feed.getSize();
				update(feed, syndFeed);
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

	public Feed add(String feedUrl) throws DownloadFeedException {
		Feed feed = new Feed();
		feed.setUrl(feedUrl);
		fetch(feed);
		feed = em.merge(feed);
		em.flush();
		return feed;
	}

	public void fetch(Feed feed) throws DownloadFeedException {
		try {
			URL feedUrl = new URL(feed.getUrl());
			SyndFeedInput syndFeedInput = new SyndFeedInput();
			SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));
			updateWithFeedContent(feed, syndFeed);
		} catch (Exception e) {
			throw new DownloadFeedException(e.getMessage());
		}
	}

	public void update(Feed feed, SyndFeed syndFeed) {
		updateFeedInformations(feed, syndFeed);
		if (feed.getId() == null) {
			em.persist(feed);
		} else {
			feed = em.merge(feed);
		}
		em.flush();
		updateFeedItems(feed, syndFeed);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void updateWithFeedContent(Feed feed, SyndFeed syndFeed) {
		try {
			feed.setInError(false);
			update(feed, syndFeed);
			em.merge(feed);
		} catch (Exception e) {
			LOG.warning(e.getMessage());
			feed.setInError(true);
			em.merge(feed);
		}
	}

	private int getInPercent(int counter, float numFeeds) {
		return (int) ((counter / numFeeds) * 100);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void update(Feed feed) {
		try {
			feed.setInError(false);
			fetch(feed);
			em.merge(feed);
		} catch (Throwable e) {
			LOG.warning(e.getMessage());
			feed.setInError(true);
			em.merge(feed);
		}
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
				Item feedItem = itemService.findItemByLink(link, feed);
				if (feedItem == null) {
					Item item = itemBuilderService.createItemFromSyndEntry(syndEntryImpl);
					item.setFeed(feed);
					em.persist(item);
					feed.setLastUpdate(Calendar.getInstance().getTime());
					feed = em.merge(feed);
					em.flush();
				}
			}
		}
	}

	private boolean mustBeUpdated(Feed feed) {
		Integer oldSize = feedSizes.get(feed);
		return oldSize == null || feed.getSize() != oldSize;
	}

}
